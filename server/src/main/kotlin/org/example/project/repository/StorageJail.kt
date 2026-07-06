package org.example.project.repository

import java.io.File

class StorageJail(private val rootStorageDir: File) {
    init {
        if (!rootStorageDir.exists()) {
            rootStorageDir.mkdirs()
        }
    }

    fun resolveAndValidatePath(relativePath: String): File {
        val targetFile = File(rootStorageDir, relativePath)
        val canonicalTarget = targetFile.canonicalFile
        val canonicalRoot = rootStorageDir.canonicalFile
        if (!canonicalTarget.path.startsWith(canonicalRoot.path)) {
            throw SecurityException("CRITICAL: Path traversal attempt detected. Access denied.")

        }
        return canonicalTarget
    }

    fun extractRelativeClientPath(canonicalFile: File): String {
        return canonicalFile.canonicalPath.removePrefix(rootStorageDir.canonicalPath)
            .removePrefix(File.separator)
    }
}