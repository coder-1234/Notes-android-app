package com.example.notes

import android.content.Intent
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

class MainActivity : AppCompatActivity() {
    private lateinit var db: NoteDB
    private lateinit var titleTxt:EditText
    private lateinit var descriptionTxt:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(applicationContext, NoteDB::class.java, "notes-db")
            .allowMainThreadQueries().build()
        titleTxt = findViewById(R.id.input_title)
        descriptionTxt = findViewById(R.id.description)
        val btn = findViewById<Button>(R.id.btn_add_note)
        btn.setOnClickListener {
            insertNewNote(titleTxt.text.toString(),descriptionTxt.text.toString())
        }
        getAllNotes()
    }

    private fun insertNewNote(t:String,d:String){
        if(t.isNotEmpty())
            db.noteDao().insertNote(Note(null, title = t, description = d))
        titleTxt.text.clear()
        descriptionTxt.text.clear()
        getAllNotes()
    }

    fun deleteNote(note:Note){
        db.noteDao().deleteNote(note)
        getAllNotes()
    }

    private fun getAllNotes(){
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        val notes: List<Note> = db.noteDao().getNotes().reversed()
        recyclerView.apply {
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    LinearLayoutManager.VERTICAL
                )
            )
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = NotesItemAdapter(this@MainActivity,notes)
        }
    }

    fun startNoteViewActivity(note:Note){
        val intent = Intent(this@MainActivity,NoteViewActivity::class.java)
        intent.putExtra("title",note.title)
        intent.putExtra("description",note.description)
        startActivity(intent)

    }
}