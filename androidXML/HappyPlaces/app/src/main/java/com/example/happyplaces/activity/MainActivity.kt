package com.example.happyplaces.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.happyplaces.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var layout: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout?.fab?.setOnClickListener {
            Intent(this, CreateHappyPlaceActivity::class.java)
                .also { startActivity(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}