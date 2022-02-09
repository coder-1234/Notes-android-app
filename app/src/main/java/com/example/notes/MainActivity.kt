package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),Interactor {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val dao = NoteDB.getDatabase(this).noteDao()
        mainViewModel = ViewModelProvider(this,MainViewModelFactory(NoteRepository(dao)))[MainViewModel::class.java]

        getAllNotes()

        binding.btnAddNote.setOnClickListener {
            insertNewNote(binding.inputTitle.text.toString(),binding.description.text.toString())
        }

        binding.searchGoBtn.setOnClickListener {
            filterNotes(binding.search.text.toString())
        }

    }

    private fun insertNewNote(t:String,d:String){
        if(t.isNotBlank()){
            mainViewModel.insertNote(Note(0,title = t, description = d))
            binding.inputTitle.text.clear()
            binding.description.text.clear()
        }
        else Toast.makeText(this,"Title cannot be empty",Toast.LENGTH_LONG).show()
    }

    override fun onClickDelete(note: Note) {
        mainViewModel.deleteNote(note)
    }

    private fun getAllNotes(){
        mainViewModel.getNotes().observe(this){ setNotesView(it) }
    }

    override fun onClickStartActivity(note: Note) {
        val intent = Intent(this@MainActivity,NoteViewActivity::class.java)
        intent.putExtra("id",note.id)
        intent.putExtra("title",note.title)
        intent.putExtra("description",note.description)
        startActivity(intent)
    }

    private fun filterNotes(searchStr:String){
        mainViewModel.filterNotes(searchStr).observe(this){setNotesView(it)}
    }

    private fun setNotesView(it:List<Note>){
        binding.myAdapter = NotesItemAdapter(this@MainActivity,it.reversed())
        binding.list.addItemDecoration(DividerItemDecoration(baseContext, LinearLayoutManager.VERTICAL))
    }
}