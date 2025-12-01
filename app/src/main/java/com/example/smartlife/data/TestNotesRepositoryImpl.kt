package com.example.smartlife.data

import com.example.smartlife.domain.Note
import com.example.smartlife.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val testList = mutableListOf<Note>().apply {
        repeat(10) {
            add(
                Note(
                    it,
                    title = "Bober",
                    content = "NUMBER OF BOBER IS $it",
                    isPinned = false,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private val notesListFlow: MutableStateFlow<List<Note>> = MutableStateFlow<List<Note>>(testList)


    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = oldList.size,
                content = content,
                title = title,
                isPinned = isPinned,
                updatedAt = updatedAt
            )
            oldList + note
        }
    }

    override suspend fun editNote(note: Note) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == note.id) {
                    note
                } else {
                    it
                }
            }
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf { it.id == noteId }
            }
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if (it.id == noteId) {
                    it.copy(isPinned = !it.isPinned)
                } else {
                    it
                }
            }
        }
    }
}