package org.example.project.repository


import io.ktor.utils.io.ByteReadChannel
import org.example.project.model.FileItem
import java.io.File

interface FileRepository {
    suspend fun listFiles(directoryPath: String): List<FileItem>
    suspend fun createDirectory(directoryPath: String, name: String): Boolean

    suspend fun saveFile(directoryPath: String, fileName: String, channel: ByteReadChannel): Boolean
    suspend fun getFile(path: String) : File
    suspend fun deleteFile(path: String): Boolean
    suspend fun renameFile(oldPath: String, newName: String): Boolean
}