package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoteViewActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_view)
        var title = intent.getStringExtra("title")
        var description = intent.getStringExtra("description")
        var titleView = findViewById<TextView>(R.id.title_txt)
        titleView.text = title
        var descriptionView = findViewById<TextView>(R.id.description_txt)
        descriptionView.text = description
        var button = findViewById<Button>(R.id.btn_main)
        button.setOnClickListener {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

}