package com.example.smartlife.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartlife.domain.ContentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentItem>>
    @Transaction
    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentItem

    @Transaction
    @Query("""
        SELECT DISTINCT notes.* FROM notes JOIN content ON content.noteId == notes.id
        WHERE title LIKE '%' || :query || '%' OR
        content LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteWithContentItem>>

    @Transaction
    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteContentItem(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteID")
    suspend fun switchPinnedStatus(noteID: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteDBModel):Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDBModel>)

    @Transaction
    suspend fun addNoteWithContent(
        noteDBModel: NoteDBModel,
        contentItem: List<ContentItem>
    ){
        val noteId = addNote(noteDBModel)
        val contentItemDbModel = contentItem.toContentItemDBModels(noteId.toInt())
        addNoteContent(contentItemDbModel)
    }

    @Transaction
    suspend fun updateNoteWithContent(
        noteDBModel: NoteDBModel,
        contentItemDbModels: List<ContentItemDBModel>
    ){
        addNote(noteDBModel)
        deleteNote(noteDBModel.id)
        addNoteContent(contentItemDbModels)
    }
}