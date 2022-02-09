package com.example.notes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notes.databinding.ActivityNoteViewBinding

class NoteViewActivity:AppCompatActivity() {
    private lateinit var binding:ActivityNoteViewBinding
    private lateinit var mainViewModel:MainViewModel
    private lateinit var title: String
    private lateinit var description:String
    private var id:Int = -1

    override fun onBackPressed() {
        if(binding.titleTxt.text.isNotBlank())
            mainViewModel.editNote(Note(id,binding.titleTxt.text.toString(),binding.descriptionTxt.text.toString()))
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_note_view)

        val dao = NoteDB.getDatabase(this).noteDao()
        mainViewModel = ViewModelProvider(this,MainViewModelFactory(NoteRepository(dao)))[MainViewModel::class.java]

        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()
        id = intent.getIntExtra("id",-1)

        binding.titleTxt.setText(title)
        binding.descriptionTxt.setText(description)

        binding.btnMain.setOnClickListener {
            onBackPressed()
        }

        binding.btnDelete.setOnClickListener {
            mainViewModel.deleteNote(Note(id,title,description))
            super.onBackPressed()
        }
    }
}