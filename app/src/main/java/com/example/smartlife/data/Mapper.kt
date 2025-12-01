package com.example.smartlife.data

import com.example.smartlife.domain.Note


fun Note.toDBModel(): NoteDBModel{
    return NoteDBModel(id,title,content,updatedAt,isPinned)
}
fun NoteDBModel.toEntity(): Note{
    return Note(id,title,content,updatedAt,isPinned)
}
fun List<NoteDBModel>.toEntities(): List<Note>{
    return this.map { it.toEntity() }
}