package com.technolovy.android.bayarberapa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.Helper.InvoiceManager
import com.technolovy.android.bayarberapa.Model.Grab
import com.technolovy.android.bayarberapa.Model.InvoiceITF
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    var invoice: InvoiceITF? = null
    var person: Double = 0.0
    var invoiceImageUri: Uri? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    lateinit var interstitialAds: InterstitialAd
    var firebaseVisionText: FirebaseVisionText? = null

    //loading state
    var isImageLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //setMobsAds()
        setButtonListener()
        setPersonQtyPicker()
        render()
    }

    fun setMobsAds() {
        MobileAds.initialize(this, "ca-app-pub-8342384986875866~7993633723")
        interstitialAds = InterstitialAd(this)
        //this is the real ads unit id
        //interstitialAds.adUnitId = "ca-app-pub-8342384986875866/6260436572"

        //this is the testing ads id
        interstitialAds.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAds.loadAd(AdRequest.Builder().build())
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

    fun setButtonListener() {
        button_choose_image.setOnClickListener {
            testSentTracker()
            if (invoice == null) {
                chooseButton()
            } else {
                processButton()
            }
        }
    }

    fun processButton() {
        Toast.makeText(this, "will process", Toast.LENGTH_LONG).show()
        val intent = Intent(this, InvoiceListPreview::class.java)
        invoice?.numOfPerson = person
        InvoiceManager.invoiceOnScreen = invoice
        InvoiceManager.firebaseVisionText = firebaseVisionText

        startActivity(intent)
    }
    fun chooseButton() {
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

    fun setPersonQtyPicker() {
        number_picker.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("number picker", "${picker.value}")
            person = picker.value.toDouble()
        }
    }

    private fun pickImageFromGallery() {
        isImageLoading = true
        render()

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
                    this.firebaseVisionText = firebaseVisionText
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
        // todo: should be added loading here
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

        isImageLoading = false
        render()

//        invoice?.processText(firebaseText)
//        invoice?.onFinishProcessInvoice = {
//            //Log.d("toast invoice","toast")
//            //Toast.makeText(this,"berhasil di baca", Toast.LENGTH_LONG).show()
//            invoiceItemsResult = it
//        }
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

            invoiceImageUri = data?.data
            Log.d("set image","sukses")
            render()
            //comment when developing to avoid fraud
//            if (interstitialAds.isLoaded) {
//                //interstitialAds.show()
//            } else {
//                //tracker ads fail to load
//                Log.d("tracker", "fail to load")
//            }
            isImageLoading = true
            render()
            data?.data?.let { recognizeText(it) }
        } else {
            isImageLoading = false
            render()
        }
    }

    /// Render Part

    fun render() {
        renderChooseImageButton()
        renderImage()
    }

    fun renderChooseImageButton() {

        if (isImageLoading) {
            button_choose_image.text = "Menunggu Gambar"
        } else if (invoice == null) {
            Log.d("render button","choose")
            button_choose_image.text = "Pilih Gambar"
        } else {
            Log.d("render button","calculate")
            button_choose_image.text = "Hitung"
        }

        Log.d("button clickable","${button_choose_image.isClickable} ${button_choose_image.isEnabled}")
        if (isImageLoading) {
            button_choose_image.isEnabled = false
            button_choose_image.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDisabled))
            button_choose_image.isClickable = false
        } else {
            button_choose_image.isEnabled = true
            button_choose_image.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
            button_choose_image.isClickable = true
        }
    }

    fun renderImage() {
        if ((invoiceImageUri == null) && !isImageLoading) {
            animation_view.setAnimation(R.raw.catplaceholder)
        } else if ((invoiceImageUri != null) && isImageLoading) {
            //loading lottie
            animation_view.setAnimation(R.raw.loading)
        } else if (invoiceImageUri != null) {
            animation_view.setImageURI(invoiceImageUri)
        }
    }


    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}
