package com.technolovy.android.bayarberapa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.Model.Grab
import com.technolovy.android.bayarberapa.Model.InvoiceITF
import com.technolovy.android.bayarberapa.Model.InvoiceItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.*


class MainActivity : AppCompatActivity() {
    var invoice: InvoiceITF? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setChooseImageButton()
    }

    fun testSentTracker() {
        var bundle = Bundle()
        val currentTime = Calendar.getInstance().getTime()
        bundle.putString("click_time", currentTime.toString())
        bundle.putString("app_version", "0.1")
        firebaseAnalytics?.logEvent("choose_image",bundle)
        if (firebaseAnalytics != null) {
            Log.d("firebaseAnalytics","logged")
        }
    }

    fun setChooseImageButton() {
        button_choose_image.setOnClickListener {
            testSentTracker()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show pop up to request access on runtime
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //no need permission under marshmallow
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick Image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun recognizeText(imgUri: Uri) {
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val imgAsFirebaseVisionImage = getFirebaseVisionImageFromUri(imgUri)
        imgAsFirebaseVisionImage?.let { image ->
            val result = detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    setInvoiceBasedOnBrand(firebaseVisionText)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Aplikasi Masih Menyiapkan Data, Mohon Coba beberapa saat lagi",
                        Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }

    fun getFirebaseVisionImageFromUri(imgUri: Uri): FirebaseVisionImage? {
        var image: FirebaseVisionImage? = null
        try {
            image = FirebaseVisionImage.fromFilePath(this, imgUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun setInvoiceBasedOnBrand(firebaseText: FirebaseVisionText) {
        if (firebaseText.text.contains("order summary", ignoreCase = true)) {
            invoice = Grab()
        } else if (firebaseText.text.contains("gojek", ignoreCase = true)) {
            //todo: init gojek
        } else {
            Toast.makeText(
                this,
                "Mohon maaf, invoice belum dikenali. Silakan gunakan fitur manual", Toast.LENGTH_LONG
            ).show()
        }

        invoice?.processText(firebaseText)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data?.data)
            Log.d("set image","sukses")
            data?.data?.let { recognizeText(it) }
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}
