package com.example.smartlife.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Database(entities = [NoteDBModel::class], version = 2, exportSchema = false)
abstract class NotesDataBase: RoomDatabase() {

    abstract fun NotesDao(): NotesDao

    companion object{
        private var instance: NotesDataBase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): NotesDataBase{
            instance?.let {
                return it
            }
            synchronized(LOCK) {
                instance?.let {
                    return it
                }
                return Room.databaseBuilder(
                    context = context,
                    klass = NotesDataBase::class.java,
                    name = "notes.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build().also {
                    instance = it
                }
            }
        }
    }
}