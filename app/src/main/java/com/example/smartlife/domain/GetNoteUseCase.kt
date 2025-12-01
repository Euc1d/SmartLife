package com.example.smartlife.domain


class GetNoteUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(id: Int): Note{
       return repository.getNote(id)
    }
}