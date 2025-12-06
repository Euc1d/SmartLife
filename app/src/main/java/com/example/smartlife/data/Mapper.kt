package com.example.smartlife.data

import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDBModel(): NoteDBModel{
    val contentDBModel = Json.encodeToString(content.toContentItemDBModels())
    return NoteDBModel(id,title,contentDBModel,updatedAt,isPinned)
}

fun List<ContentItem>.toContentItemDBModels(): List<ContentItemDBModel>{
    return map{ contentItem->
        when(contentItem){
            is ContentItem.Image -> {
                ContentItemDBModel.Image(url = contentItem.url)
            }
            is ContentItem.Text -> {
                ContentItemDBModel.Text(content = contentItem.content)
            }
        }
    }
}


fun NoteDBModel.toEntity(): Note{
    val contentNote = Json.decodeFromString<List<ContentItemDBModel>>(content).toContentItems()
    return Note(id,title,contentNote,updatedAt,isPinned)
}

fun List<ContentItemDBModel>.toContentItems(): List<ContentItem>{
    return map {
        contentItemDBModel->
        when(contentItemDBModel){
            is ContentItemDBModel.Image ->{
                ContentItem.Image(url = contentItemDBModel.url)
            }
            is ContentItemDBModel.Text -> {
            ContentItem.Text(content = contentItemDBModel.content)
            }
        }
    }
}


fun List<NoteDBModel>.toEntities(): List<Note>{
    return this.map { it.toEntity() }
}