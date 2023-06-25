package com.example.firsttrial

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.firsttrial.View.SearchData.SearchActivity
import com.example.firsttrial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var MainBinding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Tugas Akhir"

        MainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(MainBinding.root)

        pindahSeacrhActivity()
    }

    private fun pindahSeacrhActivity(){
        val btnMoveSearch = MainBinding.btnSearchActivity
        btnMoveSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_search_activity -> {
                val movesearchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(movesearchIntent)
            }

        }
    }

}