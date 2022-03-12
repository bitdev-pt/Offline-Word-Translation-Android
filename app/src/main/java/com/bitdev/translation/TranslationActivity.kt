package com.bitdev.translation

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*

class TranslationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var swapButton: ImageView
    private lateinit var spinner: Spinner
    private lateinit var textToTranslate: EditText
    private lateinit var textTranslated: EditText
    private lateinit var backButton: ImageView
    private lateinit var fontType: Typeface
    private lateinit var deleteButton: ImageView
    private lateinit var scanButton: Button

    private var languages = arrayOf("Deutsch", "English")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translation)

        spinner = findViewById(R.id.language_spinner)
        swapButton = findViewById(R.id.button_swap)
        textToTranslate = findViewById(R.id.text_to_translate)
        textTranslated = findViewById(R.id.translation_text)
        backButton = findViewById(R.id.button_back)
        scanButton = findViewById(R.id.button_camera)
        deleteButton = findViewById(R.id.button_delete)

        fontType = ResourcesCompat.getFont(this, R.font.rovas)!!

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item_text, languages)
        spinner.adapter = adapter

        swapButton.setOnClickListener {
            swapTranslations()
        }

        scanButton.setOnClickListener {
            val i = Intent(this, ScanActivity::class.java)
            startActivity(i)
        }
        deleteButton.setOnClickListener {
            textToTranslate.text.clear()
            textToTranslate.hint = getString(R.string.write_here)
            textTranslated.hint = ""
        }

        backButton.setOnClickListener {
            this.finish()
        }

        textToTranslate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Log.e("text", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textTranslated.hint = s
            }

            override fun afterTextChanged(s: Editable?) {
                //Log.e("text", s.toString())
            }

        })

    }

    private fun swapTranslations() {
        Toast.makeText(this, "ssssss", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Toast.makeText(this, pos.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

}