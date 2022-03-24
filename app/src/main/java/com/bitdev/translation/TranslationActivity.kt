package com.bitdev.translation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class TranslationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    AdapterView.OnItemClickListener {

    private var mContext: Context? = null
    private lateinit var swapButton: ImageView
    private lateinit var spinner: Spinner
    private lateinit var textToTranslate: EditText
    private lateinit var textTranslated: EditText
    private lateinit var backButton: ImageView
    private lateinit var fontType: Typeface
    private lateinit var deleteButton: ImageView
    private lateinit var scanButton: Button
    private lateinit var generalLanguage: Spinner
    private lateinit var pdfButton: Button
    private var isSwaped = false

    private val STORAGE_CODE = 1001

    lateinit var locale: Locale
    private var currentLanguage = "en"
    private var currentLang: String? = null

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
        pdfButton = findViewById(R.id.button_pdf)
        generalLanguage = findViewById(R.id.general_languages)

        if (!intent.getStringExtra("text").isNullOrEmpty()){
            textTranslated.hint = intent.getStringExtra("text")
            textToTranslate.text = intent.getStringExtra("text").toString().toEditable()
        }

        val languages =
            arrayOf(this.resources.getString(R.string.select_language), "Deutsch", "English")
        val languagesSymbol = arrayOf(this.resources.getString(R.string.language), "DE", "EN")

        mContext = this
        currentLanguage = intent.getStringExtra(currentLang).toString()

        fontType = ResourcesCompat.getFont(this, R.font.rovas)!!

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item_text, languages)
        spinner.adapter = adapter

        val generalLanguagesAdapter =
            ArrayAdapter<String>(this, R.layout.spinner_item_text, languagesSymbol)
        generalLanguage.adapter = generalLanguagesAdapter
        generalLanguage.onItemSelectedListener = this

        swapButton.setOnClickListener {
            isSwaped = !isSwaped
            swapTranslations()
        }

        pdfButton.setOnClickListener {
            if (textToTranslate.text.toString().isNotEmpty()) {
                if (spinner.selectedItemPosition != 0) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            val permission =
                                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            requestPermissions(permission, STORAGE_CODE)
                        } else {
                            savePDF()
                        }
                    } else {
                        savePDF()
                    }
                } else {
                    Toast.makeText(this, this.resources.getString(R.string.select_language), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, this.resources.getString(R.string.export_to_pdf_error), Toast.LENGTH_LONG).show()
            }
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

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun savePDF() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat(
            "yyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        val mFilePath =
            Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()

            val language = spinner.selectedItem.toString()
            val data = "Haiki Language:"+textToTranslate.text.toString() + " | " + language + " Language:" + textTranslated.hint.toString()
            mDoc.addAuthor("Francisco Collina")
            mDoc.add(Paragraph("Translations"))
            mDoc.add(Paragraph(data))
            mDoc.close()
            Toast.makeText(this, "PDF saved at $mFilePath", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("pdf_error", e.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePDF()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun swapTranslations() {
        Toast.makeText(this, isSwaped.toString(), Toast.LENGTH_SHORT).show()
        if (isSwaped) {
            textTranslated.isEnabled = true
            textTranslated.isFocusableInTouchMode = true
            textToTranslate.isFocusable = true
            textToTranslate.isEnabled = false
        } else {
            textTranslated.isEnabled = false
            textToTranslate.isEnabled = true
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (pos) {
            0 -> {
            }
            1 -> setLocale("de")
            2 -> setLocale("es")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        TODO("Not yet implemented")
    }

    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
        } else {
            Toast.makeText(
                this, "Language, , already, , selected)!", Toast.LENGTH_SHORT
            ).show()
        }
    }
}