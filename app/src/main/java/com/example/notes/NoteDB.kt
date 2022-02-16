package com.example.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDB:RoomDatabase() {
    abstract fun noteDao(): NotesDAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDB?=null

        fun getDatabase(context: Context):NoteDB{
            if(INSTANCE==null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context, NoteDB::class.java, "notes-db")
                        .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyDatabase(){
            INSTANCE = null
        }
    }
}