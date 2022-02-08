package com.example.notes

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDAO {
    @Insert
    suspend fun insertNote(note:Note)

    @Query("select * from notes_table")
    fun getNotes():LiveData<List<Note>>

    @Query("select * from notes_table where title like '%' || :s || '%'")
    fun filterNotes(s:String):LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note:Note)

    @Update
    suspend fun editNote(note: Note)

}