package com.example.smartlife.data

import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.Note

fun Note.toDBModel(): NoteDBModel {
    return NoteDBModel(id, title, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDBModels(noteId: Int): List<ContentItemDBModel> {
    return mapIndexed { index, contentItem ->
        when (contentItem) {
            is ContentItem.Image -> {
                ContentItemDBModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }

            is ContentItem.Text -> {
                ContentItemDBModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }
        }
    }
}


fun NoteWithContentItem.toEntity(): Note {
    return Note(
        note.id,
        note.title,
        content.toContentItems(),
        note.updatedAt,
        note.isPinned
    )
}

fun List<ContentItemDBModel>.toContentItems(): List<ContentItem> {
    return map { contentItemDBModel ->
        when (contentItemDBModel.contentType) {
            ContentType.TEXT -> {
                ContentItem.Text(content = contentItemDBModel.content)
            }

            ContentType.IMAGE -> {
                ContentItem.Image(url = contentItemDBModel.content)
            }
        }
    }
}


fun List<NoteWithContentItem>.toEntities(): List<Note> {
    return this.map { it.toEntity() }
}