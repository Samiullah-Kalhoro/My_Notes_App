package com.example.personalnotes.database

import android.content.Context
import androidx.room.*

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Long = 0,
    val text: String = "",
    val dateTime: Long = 0,
    val favorite: Boolean = false,
)

@Database(entities = [Note::class], version = 2)
abstract class NoteDatabase : RoomDatabase() {
    companion object {
        private var Instance: NoteDatabase? = null
        fun getInstance(context: Context): NoteDatabase {
            if (Instance == null) {
                Instance = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "notes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return Instance ?: throw Exception("Database is broken")
        }
    }
    abstract fun notesDao(): NoteDao
}