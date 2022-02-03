package com.example.notes

import androidx.room.*

@Dao
interface NotesDAO {
    @Insert()
    fun insertNote(note:Note)

    @Query("select * from notes_table")
    fun getNotes():List<Note>

    @Delete
    fun deleteNote(note:Note)

}