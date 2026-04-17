package com.example.smartlife.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "content",
    primaryKeys = ["noteId","order"],
    foreignKeys = [ForeignKey(
        entity = NoteDBModel::class,
        parentColumns = ["id"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ContentItemDBModel(
    val noteId: Int,
    val contentType: ContentType,
    val content: String,
    val order: Int
)
enum class ContentType{
    TEXT, IMAGE
}