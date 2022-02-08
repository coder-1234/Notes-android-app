package com.example.notes

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao:NotesDAO) {
    fun getNotes(): LiveData<List<Note>> {
        return noteDao.getNotes()
    }

    fun filterNotes(s:String):LiveData<List<Note>>{
        return noteDao.filterNotes(s)
    }

    suspend fun insertNote(note: Note){
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

    suspend fun editNote(note: Note){
        noteDao.editNote(note)
    }
}