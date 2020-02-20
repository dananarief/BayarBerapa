package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.helper.TrackerEvent
import com.technolovy.android.bayarberapa.helper.sendTracker
import com.technolovy.android.bayarberapa.model.InvoiceITF
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*
import kotlinx.android.synthetic.main.activity_invoice_list_preview.recycler_view
import kotlinx.android.synthetic.main.activity_invoice_list_result.*
import kotlinx.android.synthetic.main.activity_main.*

class InvoiceListResult : AppCompatActivity() {

    private var invoice: InvoiceITF? = null
    private lateinit var invoiceAdapter: InvoiceResultAdaptor
    private var isResultLoading: Boolean = true
    private lateinit var interstitialAds: InterstitialAd
    private var isAdsLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_list_result)
        setMobsAds()
        setupRecyclerView()
        setupButton()
        render()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        retrievInfoFromInvoiceManager()
        processTheImage()

    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun render() {
        renderButton()
    }

    private fun retrievInfoFromInvoiceManager() {
        invoice = InvoiceManager.invoiceOnScreen
    }

    //always comment this function when debugging
    fun setMobsAds() {
        MobileAds.initialize(this, "ca-app-pub-8342384986875866~7993633723")
        interstitialAds = InterstitialAd(this)
        //this is the real ads unit id
        interstitialAds.adUnitId = "ca-app-pub-8342384986875866/6260436572"

        //this is the testing ads id
        //interstitialAds.adUnitId = "ca-app-pub-3940256099942544/1033173712"

        interstitialAds.adListener = object: AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                isAdsLoaded = true
            }

            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAds.loadAd(AdRequest.Builder().build())
                if (invoice != null && isAdsLoaded) {
                    goToRecipientList()
                } else if (!isAdsLoaded) {
                    Toast.makeText(this@InvoiceListResult, "Sepertinya internet mu tidak nyala", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                sendTracker(TrackerEvent.adsFailToLoadOnInvoiceListResult, this@InvoiceListResult)
                isAdsLoaded = false
            }

            override fun onAdOpened() {
                super.onAdOpened()
                sendTracker(TrackerEvent.adsShowOnInvoiceListResult, this@InvoiceListResult)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                sendTracker(TrackerEvent.adsClickedOnInvoiceListResult, this@InvoiceListResult)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                sendTracker(TrackerEvent.adsImpressionOnInvoiceListResult, this@InvoiceListResult)
            }

            override fun onAdLeftApplication() {
                super.onAdLeftApplication()
                sendTracker(TrackerEvent.adsLeftOnInvoiceListResult, this@InvoiceListResult)
            }
        }

        interstitialAds.loadAd(AdRequest.Builder().build())
    }

    private fun processTheImage() {
        invoice?.invoiceItems?.let {
            invoice?.calculate(it)
            setupRecyclerView()
            invoiceAdapter.notifyDataSetChanged()
            isResultLoading = false
            render()
        }
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            invoice?.invoiceItems?.let {
                val filteredInvoiceItems = it.filter {
                    it.type == InvoiceItem.InvoiceType.PURCHASEITEM
                            || it.type == InvoiceItem.InvoiceType.SHARED_FEE
                }
                invoiceAdapter = InvoiceResultAdaptor(filteredInvoiceItems)
                adapter = invoiceAdapter
            }
        }
    }

    private fun setupButton() {
        button_tag_people.setOnClickListener {
            interstitialAds.show()
        }
    }

    private fun goToRecipientList() {
        sendTracker(TrackerEvent.tagFriendsOnResultPage, this)
        val intent = Intent(this, TagPeople::class.java)
        InvoiceManager.invoiceOnScreen = invoice
        startActivity(intent)
    }

    private fun renderButton() {
        if (isResultLoading) {
            button_tag_people.isEnabled = false
            button_tag_people.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDisabled))
            button_tag_people.isClickable = false
        } else {
            button_tag_people.isEnabled = true
            button_tag_people.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
            button_tag_people.isClickable = true
        }
    }
}
