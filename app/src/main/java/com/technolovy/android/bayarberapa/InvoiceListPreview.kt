package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceITF
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*

class InvoiceListPreview : AppCompatActivity() {
    var invoice: InvoiceITF? = null
    var firebaseVisionText: FirebaseVisionText? = null
    var invoiceItemsResult: ArrayList<InvoiceItem> = ArrayList<InvoiceItem>()
    private lateinit var invoiceAdapter: InvoicePreviewAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_list_preview)

        setupRecyclerView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        retrievInfoFromInvoiceManager()
        processTheImage()
        setupButton()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setDataDummy() {
        val invoice1: InvoiceItem = InvoiceItem()
        invoice1.name = "ayam"
        invoice1.pricePerUnit = 1000.0

        val invoice2: InvoiceItem = InvoiceItem()
        invoice2.name = "bebek"
        invoice2.pricePerUnit = 15000.0

        invoiceItemsResult.add(
            invoice1
        )

        invoiceItemsResult.add(
            invoice2
        )
    }

    fun retrievInfoFromInvoiceManager() {
        invoice = InvoiceManager.invoiceOnScreen
        firebaseVisionText = InvoiceManager.firebaseVisionText
    }

    fun processTheImage() {
        Log.d("test view","start")
        invoice?.onFinishProcessInvoice = {
            invoiceItemsResult = it
            Log.d("tests view", "${invoiceItemsResult.count()}")
            Log.d("test view","end")
            setupRecyclerView()
            invoiceAdapter.notifyDataSetChanged()
        }

        firebaseVisionText?.let {
            invoice?.processText(it)
        }
    }

    fun setupRecyclerView() {
        Log.d("test view","setup recyclerview")
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            invoiceAdapter = InvoicePreviewAdaptor(invoiceItemsResult)
            adapter = invoiceAdapter
        }
    }

    fun setupButton() {
        button_calculate.setOnClickListener {
            processButton()
        }
    }

    fun processButton() {
        Toast.makeText(this, "will process", Toast.LENGTH_LONG).show()
        val intent = Intent(this, InvoiceListResult::class.java)
        InvoiceManager.invoiceOnScreen = invoice
        InvoiceManager.firebaseVisionText = firebaseVisionText

        startActivity(intent)
    }
}
