package com.example.smartlife.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentItemDBModel{
    @Serializable
    data class Image(val url: String): ContentItemDBModel
    @Serializable
    data class Text(val content: String): ContentItemDBModel
}