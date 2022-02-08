package com.example.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.NotesItemsBinding

class NotesItemAdapter(private val context:MainActivity, val notes:List<Note>):RecyclerView.Adapter<NoteViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NotesItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        return holder.bind(context,notes[position])
    }

    override fun getItemCount(): Int = notes.size

}

class NoteViewHolder(private var bind:NotesItemsBinding):RecyclerView.ViewHolder(bind.root){

    fun bind(context: MainActivity,note:Note){
        bind.itemTitle.text = note.title
        bind.itemDescription.text = note.description
        bind.deleteButton.setOnClickListener{
            (context).deleteNote(note)
        }
        bind.root.setOnClickListener{(context).startNoteViewActivity(note)}
    }
}