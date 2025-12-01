package com.example.smartlife.domain

import kotlinx.coroutines.flow.Flow

class SearchNotesUseCase(private val repository: NotesRepository) {
    operator fun invoke(query: String): Flow<List<Note>>{
       return repository.searchNote(query)
    }
}