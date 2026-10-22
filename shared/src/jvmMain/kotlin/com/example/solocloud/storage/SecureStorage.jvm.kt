package com.example.solocloud.storage

import com.example.solocloud.dto.ServerConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64


@Serializable
private data class SecurePayload(
    val token: String? = null,
    val serverConfig: ServerConfig? = null,
    val jwtSecret:String? = null
)

actual class SecureStorage {
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }
    private val random = SecureRandom()

    private val appDir : File by lazy{
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val dir = when{
            os.contains("win") ->{
                val appData =  System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
                File(appData,"Solocloud")


            }
            os.contains("mac")->{
                File(userHome,"Library/Application Support/Solocloud")

            }
            else->{
                val configHome = System.getenv("XDG_CONFIG_HOME")
                if(configHome!=null && configHome.isNotBlank()){
                    File(configHome,"solocloud")
                }
                else{
                    File(userHome,".config/solocloud")
                }
            }
        }
        if (!dir.exists()){
            dir.mkdirs()
        }
        dir

    }
    private val storageFile:File by lazy {File(appDir,"secure.json.enc")}
    private val keyFile:File by lazy{ File(appDir,".master.key") }
    private fun getMachinePassphrase(): CharArray{
        if(keyFile.exists()){
            return keyFile.readText().toCharArray()
        }

        val randomBytes = ByteArray(32)
        random.nextBytes(randomBytes)
        val newKey = java.util.Base64.getEncoder().encodeToString(randomBytes)
        keyFile.writeText(newKey)
        keyFile.setReadable(false, false)
        keyFile.setWritable(false, false)
        keyFile.setReadable(true, true)
        keyFile.setWritable(true, true)
        return newKey.toCharArray()

    }

    private fun deriveKey(salt: ByteArray): SecretKeySpec{
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(getMachinePassphrase(),salt,65536,256)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes,"AES")

    }
    private fun encrypt(plainText: ByteArray): ByteArray {
        val salt = ByteArray(16)
        val iv = ByteArray(12)
        random.nextBytes(salt)
        random.nextBytes(iv)

        val key = deriveKey(salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val cipherText = cipher.doFinal(plainText)

        return ByteBuffer.allocate(salt.size + iv.size + cipherText.size)
            .put(salt)
            .put(iv)
            .put(cipherText)
            .array()
    }

    private fun decrypt(encryptedData: ByteArray): ByteArray{
        if (encryptedData.size < 28){
            throw IllegalArgumentException("Invalid encrypted data")

        }
        val buffer = ByteBuffer.wrap(encryptedData)
        val salt = ByteArray(16)
        val iv = ByteArray(12)
        buffer.get(salt)
        buffer.get(iv)
        val cipherText = ByteArray(buffer.remaining())
        buffer.get(cipherText)
        val key = deriveKey(salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(cipherText)
    }

    private fun readPayload():SecurePayload{
        if (!storageFile.exists() || storageFile.length() == 0L){
            return SecurePayload()

        }
        return try {
            val encryptedBytes = storageFile.readBytes()
            val decryptedBytes = decrypt(encryptedBytes)
            json.decodeFromString<SecurePayload>(decryptedBytes.decodeToString())

        }catch (_:Exception){
            SecurePayload()
        }
    }

    private fun writePayload(payload: SecurePayload){
        val rawJson  = json.encodeToString(payload).encodeToByteArray()
        val encryptedBytes = encrypt(rawJson)
        val tempFile = File(storageFile.parentFile,"${storageFile.name}.tmp")
        tempFile.writeBytes(encryptedBytes)
        Files.move(
            tempFile.toPath(),
            storageFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    actual suspend fun saveToken(token: String) {
        mutex.withLock {
            val current = readPayload()
            writePayload(current.copy(token = token))

        }
    }

    actual suspend fun getToken(): String? {
        return mutex.withLock {
            readPayload().token
        }
    }

    actual suspend fun clearToken() {
        mutex.withLock {
            val current = readPayload()
            writePayload(current.copy(token = null))
        }
    }

    actual suspend fun saveServerConfig(config: ServerConfig) {
        mutex.withLock {
            val current = readPayload()
            writePayload(current.copy(serverConfig = config))
        }
    }

    actual suspend fun getServerConfig(): ServerConfig? {
        return mutex.withLock {
            readPayload().serverConfig
        }
    }

    actual suspend fun saveJwtSecret(secret: String) {
        mutex.withLock {
            val current = readPayload()
            writePayload(current.copy(jwtSecret = secret))
        }
    }

    actual suspend fun getJwtSecret(): String? {
        return mutex.withLock {
            readPayload().jwtSecret
        }
    }
}