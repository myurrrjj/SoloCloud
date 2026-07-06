package org.example.project.routes

import io.ktor.http.ContentRange
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.io.files.FileNotFoundException
import org.example.project.model.FileItem
import org.example.project.repository.FileRepository
import java.io.File
import java.io.FileInputStream

//import java.net.http.HttpHeaders

fun Application.fileRoutes(repository: FileRepository) {
    routing {
        authenticate("auth-jwt") {
            route("/api/v1") {
                get("files") {
                    val path = call.request.queryParameters["path"] ?: ""
                    val items = repository.listFiles(path)
                    call.respond(HttpStatusCode.OK, items)

                }

                get("download") {
                    val path = call.request.queryParameters["path"]
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'path' parameter")

                    val file: File = try {
                        repository.getFile(path)
                    } catch (e: FileNotFoundException) {
                        return@get call.respond(HttpStatusCode.NotFound, "File not found")
                    } catch (e: SecurityException) {
                        return@get call.respond(HttpStatusCode.Forbidden, "Access denied")
                    }

                    val fileSize = file.length()
                    val lastModified = file.lastModified()

                    val rangeHeader = call.request.headers[HttpHeaders.Range]
                    if (rangeHeader != null) {
                        val range = parseRange(rangeHeader, fileSize)
                        if (range == null) {
                            call.respond(HttpStatusCode.RequestedRangeNotSatisfiable, "Invalid range")
                            return@get
                        }
                        val (start, end) = range
                        val contentLength = end - start + 1
                        call.response.headers.append(HttpHeaders.ContentRange, "bytes $start-$end/$fileSize")
                        call.response.status(HttpStatusCode.PartialContent)
                        call.respondOutputStream(contentType = null, status = HttpStatusCode.PartialContent) {
                            FileInputStream(file).use { input ->
                                input.skip(start)
                                val buffer = ByteArray(8192)
                                var remaining = contentLength
                                while (remaining > 0) {
                                    val read = input.read(buffer, 0, minOf(buffer.size.toLong(), remaining).toInt())
                                    if (read <= 0) break
                                    write(buffer, 0, read)
                                    remaining -= read
                                }
                                flush()
                            }
                        }
                    } else {
                        call.response.headers.append(HttpHeaders.ContentLength, fileSize.toString())
                        call.response.headers.append(HttpHeaders.LastModified, lastModified.toString())
                        call.respondOutputStream(contentType = null, status = HttpStatusCode.OK) {
                            FileInputStream(file).use { input ->
                                input.copyTo(this)
                            }
                        }
                    }
                }
                post("directories") {
                    val parent = call.request.queryParameters["parent"] ?: ""
                    val name = call.request.queryParameters["name"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing 'name' parameter"
                        )
                    val success = repository.createDirectory(parent, name)
                    if (success) call.respond(HttpStatusCode.Created, "Directory Created")
                    else call.respond(
                        HttpStatusCode.InternalServerError,
                        "Failed to create directory"
                    )

                }
                post("upload") {
                    val path = call.request.queryParameters["path"] ?: ""
                    val multipart = call.receiveMultipart()
                    var uploaded = false
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val fileName = part.originalFileName ?: "Unnamed"
                            val success = repository.saveFile(path, fileName, part.provider())
                            uploaded = success

                        }
                        part.dispose()
                    }
                    if (uploaded) call.respond(HttpStatusCode.Created, "File Uploaded")
                    else call.respond(HttpStatusCode.InternalServerError, "Upload Failed")

                }
                delete("files") {
                    val path = call.request.queryParameters["path"]
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing 'path' parameter"
                        )
                    val success = repository.deleteFile(path)
                    if (success) call.respond(HttpStatusCode.NoContent)
                    else call.respond(HttpStatusCode.InternalServerError, "Deletion failed")
                }

                post("rename") {
                    val oldPath = call.request.queryParameters["oldPath"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing 'oldPath' parameter"
                        )
                    val newName = call.request.queryParameters["newName"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing 'newName' parameter"
                        )
                    val success = repository.renameFile(oldPath, newName)
                    if (success) call.respond(HttpStatusCode.OK, "Renamed successfully")
                    else call.respond(HttpStatusCode.InternalServerError, "Rename failed")
                }
            }
        }
    }
}