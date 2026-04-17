package com.example.smartlife.data

import com.example.smartlife.domain.ContentItem
import com.example.smartlife.domain.Note
import com.example.smartlife.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val imageManager: InternalStorageImageFileManager
) : NotesRepository {


    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val processedContent = content.processForStorage()
        val noteDBModel = NoteDBModel(
            id = 0,
            title = title,
            updatedAt = updatedAt,
            isPinned = isPinned
        )
        notesDao.addNoteWithContent(
            noteDBModel,
            processedContent
        )
    }

    private suspend fun List<ContentItem>.processForStorage(): List<ContentItem> {
        return map { contentItem ->
            when (contentItem) {
                is ContentItem.Image -> {
                    if (imageManager.isInternal(contentItem.url)) {
                        contentItem
                    } else {
                        val newPath = imageManager.saveToInternalStorage(contentItem.url)
                        contentItem.copy(url = newPath)
                    }
                }

                is ContentItem.Text -> {
                    contentItem
                }
            }
        }
    }


    override suspend fun editNote(note: Note) {
        val oldContentItems = notesDao.getNote(note.id).toEntity()
        val newContentItems = note

        val oldURls = oldContentItems.content.filterIsInstance<ContentItem.Image>().map { it.url }
        val newURls = newContentItems.content.filterIsInstance<ContentItem.Image>().map { it.url }


        val diff = oldURls - newURls
        diff.forEach {
            imageManager.deleteFromInternalStorage(it)
        }
        val processedContent = note.content.processForStorage()
        val contentItems = note.copy(content = processedContent )

       notesDao.updateNoteWithContent(
           contentItems.toDBModel(),
           processedContent.toContentItemDBModels(note.id)
       )

    }

    override suspend fun deleteNote(noteId: Int) {
        val noteToDelete = notesDao.getNote(noteId).toEntity()
        notesDao.deleteNote(noteId)
        noteToDelete.content.filterIsInstance<ContentItem.Image>().forEach {
            imageManager.deleteFromInternalStorage(it.url)
        }
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
}