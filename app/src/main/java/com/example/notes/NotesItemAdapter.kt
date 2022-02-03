package com.example.notes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesItemAdapter(private val context:MainActivity, val notes:List<Note>):RecyclerView.Adapter<NoteViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_items, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        return holder.bind(context,notes[position])
    }

    override fun getItemCount(): Int = notes.size

}

class NoteViewHolder(private var v: View):RecyclerView.ViewHolder(v){
    private val titleText = v.findViewById<TextView>(R.id.item_title)
    private val subtitleText = v.findViewById<TextView>(R.id.item_description)
    private val deleteBtn = v.findViewById<ImageButton>(R.id.deleteButton)

    fun bind(context: MainActivity,note:Note){
        titleText.text = note.title
        subtitleText.text = note.description
        deleteBtn.setOnClickListener{
            (context).deleteNote(note)
        }
        v.setOnClickListener{(context).startNoteViewActivity(note)}
    }
}