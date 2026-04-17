package com.example.smartlife.data

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithContentItem(
    @Embedded
    val note: NoteDBModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val content: List<ContentItemDBModel>
)