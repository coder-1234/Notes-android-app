package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(),Interactor {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleDriveService: Drive
    private lateinit var getResult:ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        googleDriveService = GoogleDriveServiceProvider(this).getService()

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
        }

        val dao = NoteDB.getDatabase(this).noteDao()
        mainViewModel = ViewModelProvider(this,MainViewModelFactory(NoteRepository(dao)))[MainViewModel::class.java]

        val dialog = LoadingDialog(this)
        dialog.showDialog()
        CoroutineScope(Dispatchers.IO).launch {
            download()
            withContext(Main){
                dialog.dismissDialog()
                getAllNotes()
            }
        }

        binding.btnAddNote.setOnClickListener {
            insertNewNote(binding.inputTitle.text.toString(),binding.description.text.toString())
        }

        binding.searchGoBtn.setOnClickListener {
            filterNotes(binding.search.text.toString())
        }

        binding.logoutBtn.setOnClickListener {
            handleLogout()
        }

        binding.sync.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                deleteAll()
                upload()
            }
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

    private fun handleLogout(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                val intent = Intent(this,SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
    }

    private fun upload(){

        val dbPath = "/data/data/com.example.notes/databases/notes-db"
        val dbPathShm = "/data/data/com.example.notes/databases/notes-db-shm"
        val dbPathWal = "/data/data/com.example.notes/databases/notes-db-wal"

        val storageFile = File()
        storageFile.parents = Collections.singletonList("appDataFolder")
        storageFile.name = "notes-db"

        val storageFileShm = File()
        storageFileShm.parents = Collections.singletonList("appDataFolder")
        storageFileShm.name = "notes-db-shm"

        val storageFileWal = File()
        storageFileWal.parents = Collections.singletonList("appDataFolder")
        storageFileWal.name = "notes-db-wal"

        val mediaContent = FileContent("", java.io.File(dbPath))
        val mediaContentShm = FileContent("", java.io.File(dbPathShm))
        val mediaContentWal = FileContent("", java.io.File(dbPathWal))

        try {
            val file = googleDriveService.files().create(storageFile, mediaContent).execute()
            val fileShm = googleDriveService.files().create(storageFileShm, mediaContentShm).execute()
            val fileWal = googleDriveService.files().create(storageFileWal, mediaContentWal).execute()

        }
        catch (ex:UserRecoverableAuthIOException){
            getResult.launch(ex.intent)
        }
        catch(e:Exception){
            e.printStackTrace()
        }

    }

    private fun deleteAll(){
        try {
            val files = googleDriveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()
            for (file in files.files) {
                when (file.name) {
                    "notes-db","notes-db-shm","notes-db-wal" -> {
                        googleDriveService.files().delete(file.id).execute()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun download() {
        val dbPath = "/data/data/com.example.notes/databases/notes-db"
        val dbPathShm = "/data/data/com.example.notes/databases/notes-db-shm"
        val dbPathWal = "/data/data/com.example.notes/databases/notes-db-wal"

        try {
            val dir = java.io.File("/data/data/com.example.notes/databases")
            if (dir.isDirectory) {
                val children = dir.list()
                if(children!=null)
                    for (i in children.indices) {
                        java.io.File(dir, children[i]).delete()
                    }
            }
            val files = googleDriveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()
            if (files.files.size == 0) Log.e("hello", "No DB file exists in Drive")
            for (file in files.files) {
                System.out.printf(
                    "Found file: %s (%s) %s\n",
                    file.name, file.id, file.createdTime
                )
                when (file.name) {
                    "notes-db" -> {
                        val outputStream = FileOutputStream(dbPath)
                        googleDriveService.files()[file.id].executeMediaAndDownloadTo(outputStream)
                    }
                    "notes-db-shm" -> {
                        val outputStream = FileOutputStream(dbPathShm)
                        googleDriveService.files()[file.id].executeMediaAndDownloadTo(outputStream)
                    }
                    "notes-db-wal" -> {
                        val outputStream = FileOutputStream(dbPathWal)
                        googleDriveService.files()[file.id].executeMediaAndDownloadTo(outputStream)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        NoteDB.destroyDatabase()
        super.onDestroy()
    }

}