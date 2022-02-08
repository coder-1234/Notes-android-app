package com.example.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel(){

    fun getNotes() : LiveData<List<Note>> {
        return noteRepository.getNotes()
    }

    fun filterNotes(s:String) : LiveData<List<Note>> {
        return noteRepository.filterNotes(s)
    }

    fun insertNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.insertNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.deleteNote(note)
        }
    }

    fun editNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.editNote(note)
        }
    }
}
