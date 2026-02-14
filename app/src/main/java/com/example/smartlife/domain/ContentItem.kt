package com.example.smartlife.domain

sealed interface ContentItem {
    data class Image(val url: String): ContentItem
    data class Text(val content: String): ContentItem
}