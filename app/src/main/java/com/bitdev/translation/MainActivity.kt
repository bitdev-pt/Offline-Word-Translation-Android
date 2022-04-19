package com.bitdev.translation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startButton = findViewById(R.id.start_button)

        startButton.setOnClickListener {
            val i = Intent(this, TranslationActivity::class.java)
            startActivity(i)
        }
    }
}
