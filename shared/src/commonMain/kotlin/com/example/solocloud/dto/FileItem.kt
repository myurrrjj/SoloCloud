package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileItem(
    @SerialName("name")
    val name : String,

    @SerialName("path")
    val path:String,

    @SerialName("isDirectory")
    val isDirectory : Boolean,

    @SerialName("size")
    val size : Long,

    @SerialName("modifiedAt")
    val modifiedAt: Long

)