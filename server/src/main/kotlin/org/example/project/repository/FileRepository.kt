package org.example.project.repository


import io.ktor.utils.io.ByteReadChannel
import org.example.project.model.FileItem

interface FileRepository {
    suspend fun listFiles(directoryPath: String): List<FileItem>
    suspend fun createDirectory(directoryPath: String, name: String): Boolean

    suspend fun saveFile(directoryPath: String, fileName:String, channel: ByteReadChannel):Boolean
}