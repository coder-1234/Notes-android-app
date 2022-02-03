package com.example.notes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDB:RoomDatabase() {
    abstract fun noteDao(): NotesDAO
}