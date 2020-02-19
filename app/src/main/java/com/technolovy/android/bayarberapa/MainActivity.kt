package com.technolovy.android.bayarberapa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.technolovy.android.bayarberapa.helper.*
import com.technolovy.android.bayarberapa.model.Grab
import com.technolovy.android.bayarberapa.model.InvoiceITF
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var invoice: InvoiceITF? = null
    private var person: Double = 1.0
    private var invoiceImageUri: Uri? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var interstitialAds: InterstitialAd
    private var firebaseVisionText: FirebaseVisionText? = null

    //loading state
    private var isImageLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InvoiceManager.journeyId = UUID.randomUUID().toString()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setMobsAds()
        setImageInfoTextListener()
        setButtonListener()
        setPersonQtyPicker()
        render()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_page_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (item.itemId == R.id.more_menu) {
                sendTracker(TrackerEvent.openAboutApps, this)
                val intent = Intent(this, AboutAppsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //always comment this function when debugging
    fun setMobsAds() {
        MobileAds.initialize(this, "ca-app-pub-8342384986875866~7993633723")
        interstitialAds = InterstitialAd(this)
        //this is the real ads unit id
        //interstitialAds.adUnitId = "ca-app-pub-8342384986875866/6260436572"

        //this is the testing ads id
        interstitialAds.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAds.loadAd(AdRequest.Builder().build())
    }

    private fun setButtonListener() {
        button_choose_image.setOnClickListener {
            if (invoice == null) {
                chooseButton()
            } else {
                processButton()
            }
        }
    }

    private fun setImageInfoTextListener() {
        place_holder_text.setOnClickListener {
            sendTracker(TrackerEvent.deleteImageOnUploadInvoice,this)
            if (invoice != null) {
                invoice = null
                invoiceImageUri = null
                InvoiceManager.invoiceImg = null
                InvoiceManager.recipientList.clear()
                InvoiceManager.firebaseVisionText = null
                InvoiceManager.invoiceOnScreen = null
                render()
            }
        }
    }

    private fun processButton() {
        sendTracker(TrackerEvent.processImageOnUploadInvoice,this)
        val intent = Intent(this, InvoiceListPreview::class.java)
        invoice?.numOfPerson = person
        InvoiceManager.invoiceOnScreen = invoice
        InvoiceManager.firebaseVisionText = firebaseVisionText
        InvoiceManager.invoiceImg = invoiceImageUri

        //comment when developing to avoid fraud
        if (interstitialAds.isLoaded) {
            sendTracker(TrackerEvent.adsShowOnUploadInvoicePage, this)
            interstitialAds.show()
        } else {
            sendTrackerError(TrackerEvent.adsFailToLoadOnUploadInvoicePage, this, "fait to load ads")
        }

        startActivityForResult(intent, GO_TO_PREVIEW_SCREEN)
    }

    private fun chooseButton() {
        sendTracker(TrackerEvent.chooseImageOnUploadInvoice,this)
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

    private fun setPersonQtyPicker() {
        number_picker.setOnValueChangedListener { picker, _, _ ->
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

    private fun recognizeText(imgUri: Uri) {
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val imgAsFirebaseVisionImage = getFirebaseVisionImageFromUri(imgUri)
        imgAsFirebaseVisionImage?.let { image ->
            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    this.firebaseVisionText = firebaseVisionText
                    setInvoiceBasedOnBrand(firebaseVisionText)
                }
                .addOnFailureListener { e ->
                    sendTrackerError(TrackerEvent.errorProcessImage, this, e.toString())
                    Toast.makeText(
                        this,
                        "Aplikasi Masih Menyiapkan Data, Mohon Coba beberapa saat lagi",
                        Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }

    private fun getFirebaseVisionImageFromUri(imgUri: Uri): FirebaseVisionImage? {
        var image: FirebaseVisionImage? = null
        try {
            image = FirebaseVisionImage.fromFilePath(this, imgUri)
        } catch (e: IOException) {
            sendTrackerError(TrackerEvent.errorVisionImage, this, e.toString())
            e.printStackTrace()
        }
        return image
    }

    private fun setInvoiceBasedOnBrand(firebaseText: FirebaseVisionText) {
        // todo: should be added loading here
        if (firebaseText.text.contains("order summary", ignoreCase = true)) {
            invoice = Grab()
        } else if (firebaseText.text.contains("gojek", ignoreCase = true)) {
            //todo: init gojek
            sendTrackerError(TrackerEvent.errorRecognizeBrand, this, "brand gojek")
            //implement later
        } else {
            sendTrackerError(TrackerEvent.errorRecognizeBrand, this, "brand not found")
            Toast.makeText(
                this,
                "Mohon maaf, invoice belum dikenali. Silakan gunakan fitur manual", Toast.LENGTH_LONG
            ).show()
        }

        isImageLoading = false
        render()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    sendTrackerError(TrackerEvent.errorPermissionDenied, this, "permission not granted")
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            invoiceImageUri = data?.data
            render()
            isImageLoading = true
            render()
            data?.data?.let { recognizeText(it) }
        } else {
            isImageLoading = false
            render()

            if (requestCode == GO_TO_PREVIEW_SCREEN) {
                InvoiceManager.recipientList.clear()
            }
        }
    }

    /// Render Part
    private fun render() {
        renderImageInfoText()
        renderChooseImageButton()
        renderImage()
    }

    private fun renderImageInfoText() {
        if (invoice == null) {
            place_holder_text.text = "Silakan ulpload invoice Grab mu..."
            place_holder_text.setTextColor(ContextCompat.getColor(this,android.R.color.tab_indicator_text))
        } else {
            place_holder_text.text = "Hapus Gambar"
            place_holder_text.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
        }
    }

    private fun renderChooseImageButton() {
        if (isImageLoading) {
            button_choose_image.text = getString(R.string.waiting_pict_button)
        } else if (invoice == null) {
            button_choose_image.text = getString(R.string.choose_pict_button)
        } else {
            button_choose_image.text = getString(R.string.process_pict_button)
        }

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

    private fun renderImage() {
        if ((invoiceImageUri == null) && !isImageLoading) {
            animation_view.setAnimation(R.raw.catplaceholder)
        } else if ((invoiceImageUri != null) && isImageLoading) {
            //loading lottie
            animation_view.setAnimation(R.raw.loading)
        } else if (invoiceImageUri != null) {
            animation_view.setImageURI(invoiceImageUri)
        }
        animation_view.playAnimation()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val GO_TO_PREVIEW_SCREEN = 1002
    }
}
