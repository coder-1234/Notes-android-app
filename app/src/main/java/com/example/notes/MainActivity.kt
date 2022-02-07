package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var db: NoteDB
    private lateinit var titleTxt:EditText
    private lateinit var descriptionTxt:EditText
    private lateinit var searchTxt:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = NoteDB.getDatabase(this)
        titleTxt = findViewById(R.id.input_title)
        descriptionTxt = findViewById(R.id.description)
        searchTxt = findViewById(R.id.search)
        val btn = findViewById<Button>(R.id.btn_add_note)
        getAllNotes()
        btn.setOnClickListener {
            insertNewNote(titleTxt.text.toString(),descriptionTxt.text.toString())
        }
        val btnSearch = findViewById<Button>(R.id.search_go_btn)
        btnSearch.setOnClickListener {
            filterNotes(searchTxt.text.toString())
        }
    }

    private fun insertNewNote(t:String,d:String){
        if(t.isNotEmpty()){
            db.noteDao().insertNote(Note(null, title = t, description = d))
        }
        titleTxt.text.clear()
        descriptionTxt.text.clear()
    }

    fun deleteNote(note:Note){
        db.noteDao().deleteNote(note)
    }

    private fun getAllNotes(){
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        db.noteDao().getNotes().observe(this){
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

    fun startNoteViewActivity(note:Note){
        val intent = Intent(this@MainActivity,NoteViewActivity::class.java)
        intent.putExtra("title",note.title)
        intent.putExtra("description",note.description)
        startActivity(intent)
    }

    private fun filterNotes(searchStr:String){
        if(searchStr.isEmpty()){
            getAllNotes()
        } else {
            val recyclerView = findViewById<RecyclerView>(R.id.list)
            db.noteDao().filterNotes(searchStr).observe(this){
                recyclerView.apply {
                    addItemDecoration(
                        DividerItemDecoration(
                            baseContext,
                            LinearLayoutManager.VERTICAL
                        )
                    )
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = NotesItemAdapter(this@MainActivity, it.reversed())
                }
            }
        }
    }
}