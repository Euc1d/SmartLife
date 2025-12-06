package com.example.smartlife.data

import android.content.Context
import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.Note
import com.example.smartlife.domain.NotesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class NoteRepositoryImpl @Inject constructor(val notesDao: NotesDao): NotesRepository {



    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        notesDao.addNote(
            Note(
                id = 0,
                title = title,
                content = content,
                isPinned = isPinned,
                updatedAt = updatedAt
            ).toDBModel()
        )
    }

    override suspend fun editNote(note: Note) {
        return notesDao.addNote(note.toDBModel())
    }

    override suspend fun deleteNote(noteId: Int) {
        return notesDao.deleteNote(noteId)
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map {
            it.toEntities()
        }
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map {
            it.toEntities()
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }
//    companion object{
//        private val LOCK = Any()
//        private var instance : NoteRepositoryImpl? = null
//
//        fun getInstance(): NoteRepositoryImpl{
//            instance?.let {
//                return it
//            }
//            synchronized(LOCK) {
//                instance?.let {
//                    return it
//                }
//                return NoteRepositoryImpl().also { instance = it }
//            }
//        }
//
//    }
}