package com.example.smartlife.domain

class AddNoteUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(
        title: String,
        content: String,
        isPinned: Boolean = false,
        updatedAt: Long = System.currentTimeMillis()
    ) {
        repository.addNote(
            title = title,
            content = content,
            isPinned = isPinned,
            updatedAt = updatedAt
        )
    }
}