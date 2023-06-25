package com.example.firsttrial.View.SearchData

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firsttrial.Adapter.SearchDataAdapter
import com.example.firsttrial.R
import com.example.firsttrial.databinding.ActivitySearchBinding
import java.io.File
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var SearchBinding : ActivitySearchBinding
    private lateinit var rvDaftarAudio : RecyclerView
    private lateinit var adapter: SearchDataAdapter
    private val MEDIA_PATH = Environment.getExternalStorageDirectory().path+"/"
    private val songList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SearchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(SearchBinding.root)

        supportActionBar?.title = "Search Audio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvDaftarAudio = findViewById(R.id.rv_daftaraudio)
        rvDaftarAudio.setHasFixedSize(true)
        rvDaftarAudio.layoutManager = LinearLayoutManager(this)

        showRecyclerList()

        Log.e("Media path", MEDIA_PATH)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getAllAudioFiles()
        }
        searchAudio()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllAudioFiles() {
        if (MEDIA_PATH != null) {
            val mainFile = File(MEDIA_PATH)
            val fileList = mainFile.listFiles()

            for (file in fileList) {
                Log.e("Media path", file.toString())

                if(file.isDirectory){
                    scanDirectory(file)
                }
                else {
                    val path = file.absolutePath
                    if (path.endsWith(".mp3")){
                        songList.add(path)
                    } else if (path.endsWith(".wav")){
                        songList.add(path)

                    } else if (path.endsWith(".aac")){
                        songList.add(path)

                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }

        adapter = SearchDataAdapter(songList)
        rvDaftarAudio.adapter = adapter
    }

    private fun scanDirectory(directory: File) {
        if (directory != null) {
            val fileList = directory.listFiles()
            if(fileList != null){
                for (file in fileList) {
                    Log.e("Media path", file.toString())

                    if (file.isDirectory) {
                        scanDirectory(file)
                    } else {
                        val path = file.absolutePath
                        if (path.endsWith(".mp3")) {
                            songList.add(path)
                        } else if (path.endsWith(".wav")){
                            songList.add(path)
                        } else if(path.endsWith(".aac")){
                            songList.add(path)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllAudioFiles()
        }
    }

    private fun searchAudio(){SearchBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            SearchBinding.search.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            filterList(newText)
            return true
        }

    })}

    private fun filterList(query : String?){
        if (query != null){
            val filteredList = ArrayList<String>()
            for (song in songList){
                if (song.toLowerCase(Locale.ROOT).contains(query)){
                    filteredList.add(song)
                }
            }

            if(filteredList.isEmpty()){
                Toast.makeText(this, " No Audio Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showRecyclerList(){
        rvDaftarAudio.layoutManager = LinearLayoutManager(this)
        var searchAdapter = SearchDataAdapter(songList)
        rvDaftarAudio.adapter = searchAdapter
    }

}