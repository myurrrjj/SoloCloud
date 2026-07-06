package org.example.project.repository

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.files.FileNotFoundException
import org.example.project.model.FileItem
import java.io.File

class LocalFileRepository(private val jail: StorageJail) : FileRepository {
    override suspend fun listFiles(directoryPath: String): List<FileItem> = withContext(
        Dispatchers.IO
    ) {
        val dir = jail.resolveAndValidatePath(directoryPath)
        if (!dir.exists() || !dir.isDirectory) {
            throw FileNotFoundException("Directory not found.")
        }

        dir.listFiles()?.map { file ->
            FileItem(
                id = file.absolutePath.hashCode().toString(),
                name = file.name,
                path = jail.extractRelativeClientPath(file),
                sizeBytes = file.length(),
                isDirectory = file.isDirectory
            )
        } ?: emptyList()
    }

    override suspend fun createDirectory(
        directoryPath: String, name: String
    ): Boolean = withContext(Dispatchers.IO) {
        val parentDir = jail.resolveAndValidatePath(directoryPath)
        val newDir = jail.resolveAndValidatePath("$directoryPath/${name}")

        if (!parentDir.exists() || !parentDir.isDirectory) {
            return@withContext false
        }

        newDir.mkdir()
    }

    override suspend fun saveFile(
        directoryPath: String, fileName: String, channel: ByteReadChannel
    ): Boolean = withContext(Dispatchers.IO) {
        val targetDir = jail.resolveAndValidatePath(directoryPath)
        val tempFile = File(targetDir, "$fileName.tmp")
        val finalFile = File(targetDir, fileName)

        try {
            tempFile.outputStream().use { output ->
                channel.copyTo(output)
            }
            tempFile.renameTo(finalFile)

        } catch (e: Exception) {
            tempFile.delete()
            false
        }
    }
}