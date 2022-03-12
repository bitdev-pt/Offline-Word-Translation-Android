package com.bitdev.translation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

class ScanActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        backButton = findViewById(R.id.button_back_scan)

        backButton.setOnClickListener {
            this.finish()
        }
    }
}