package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        }
        else {
            Toast.makeText(this,"Title cannot be empty",Toast.LENGTH_LONG).show()
        }
        binding.inputTitle.text.clear()
        binding.description.text.clear()
    }

    override fun onClickDelete(note: Note) {
        mainViewModel.deleteNote(note)
    }

    private fun getAllNotes(){
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        mainViewModel.getNotes().observe(this){
            recyclerView.apply {
                addItemDecoration(
                    DividerItemDecoration(
                        baseContext,
                        LinearLayoutManager.VERTICAL
                    )
                )
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = NotesItemAdapter(this@MainActivity,it.reversed())
            }
        }
    }

    override fun onClickStartActivity(note: Note) {
        val intent = Intent(this@MainActivity,NoteViewActivity::class.java)
        intent.putExtra("id",note.id)
        intent.putExtra("title",note.title)
        intent.putExtra("description",note.description)
        startActivity(intent)
    }

    private fun filterNotes(searchStr:String){
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        mainViewModel.filterNotes(searchStr).observe(this){
            recyclerView.apply {
                addItemDecoration(
                    DividerItemDecoration(
                        baseContext,
                        LinearLayoutManager.VERTICAL
                    )
                )
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = NotesItemAdapter(this@MainActivity,it.reversed())
            }
        }

    }
}