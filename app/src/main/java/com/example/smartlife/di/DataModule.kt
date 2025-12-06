package com.example.smartlife.di

import android.content.Context
import androidx.activity.contextaware.ContextAware
import com.example.smartlife.data.NoteRepositoryImpl
import com.example.smartlife.data.NotesDao
import com.example.smartlife.data.NotesDataBase
import com.example.smartlife.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindRepositoryImpl( notesRepositoryImpl: NoteRepositoryImpl ): NotesRepository

    companion object {

        @Singleton
        @Provides
        fun providesNotesDataBase(@ApplicationContext context: Context): NotesDataBase {
            return NotesDataBase.getInstance(context)
        }

        @Singleton
        @Provides
        fun providesNotesDao(notesDataBase: NotesDataBase): NotesDao {
            return notesDataBase.NotesDao()
        }
    }
}