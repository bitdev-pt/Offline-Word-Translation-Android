package com.bitdev.translation

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions

class ScanActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var startCameraButton: Button
    private lateinit var professorImage: ImageView
    private lateinit var layoutChat: LinearLayout
    private lateinit var imagePreview: ImageView
    private lateinit var cancelButton: Button
    private lateinit var hintText: ImageView
    private lateinit var hintImage: ImageView
    private lateinit var textLayout: ConstraintLayout
    private lateinit var textFoundHint: TextView
    private lateinit var correctScan: Button
    private lateinit var textFounded: TextView

    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var recognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // When using Japanese script library
        recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

        backButton = findViewById(R.id.button_back_scan)
        startCameraButton = findViewById(R.id.button_start_camera)
        professorImage = findViewById(R.id.professor_image)
        layoutChat = findViewById(R.id.chat)
        imagePreview = findViewById(R.id.image_preview)
        cancelButton = findViewById(R.id.cancel_image)
        hintText = findViewById(R.id.hint)
        hintImage = findViewById(R.id.hint_image)
        textLayout = findViewById(R.id.textRecon_layout)
        textFoundHint = findViewById(R.id.text_founded_hint)
        correctScan = findViewById(R.id.correct_scan)
        textFounded = findViewById(R.id.text_founded)

        startCameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
        backButton.setOnClickListener {
            finish()
        }

        correctScan.setOnClickListener {
            sendTextToTranslation()
        }
        cancelButton.setOnClickListener {
            showImageAndText(null, false, "", false)
        }
    }

    private fun sendTextToTranslation() {
        val text = textFounded.text.toString()
        val intent = Intent(this, TranslationActivity::class.java)
        intent.putExtra("text", text)
        startActivity(intent)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            recognizeText(imageBitmap)
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.text.isEmpty()) {
                    showImageAndText(bitmap, true, "", showText = true)
                } else {
                    showImageAndText(bitmap, true, visionText.text, showText = true)
                }
            }
            .addOnFailureListener { e ->
                showImageAndText(bitmap, true, "", false)
                Log.e("error", e.toString())
            }
    }

    fun showImageAndText(image: Bitmap?, showImage: Boolean, text: String, showText: Boolean) {
        if (showImage) {
            professorImage.visibility = View.GONE
            layoutChat.visibility = View.GONE
            hintText.visibility = View.GONE
            imagePreview.setImageBitmap(image)
            cancelButton.visibility = View.VISIBLE
            imagePreview.visibility = View.VISIBLE
            hintImage.visibility = View.GONE
            if (showText) {
                textLayout.visibility = View.VISIBLE
                if (text.isEmpty()) {
                    textFoundHint.text = this.resources.getString(R.string.text_not_found)
                    textFounded.text = null
                    textFounded.visibility = View.GONE
                    correctScan.visibility = View.GONE
                } else {
                    textFoundHint.text = this.resources.getString(R.string.text_founded)
                    textFounded.text = text
                    textFounded.visibility = View.VISIBLE
                    correctScan.visibility = View.VISIBLE
                }
            }
        } else {
            professorImage.visibility = View.VISIBLE
            layoutChat.visibility = View.VISIBLE
            hintText.visibility = View.VISIBLE
            cancelButton.visibility = View.GONE
            imagePreview.setImageBitmap(null)
            imagePreview.visibility = View.GONE
            hintImage.visibility = View.VISIBLE
            textFoundHint.text = this.resources.getString(R.string.text_founded)
            textFounded.visibility = View.GONE
            textLayout.visibility = View.GONE
            correctScan.visibility = View.GONE
        }
    }
}