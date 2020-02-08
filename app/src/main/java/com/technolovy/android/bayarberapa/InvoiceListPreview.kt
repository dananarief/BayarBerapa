package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceITF
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*

class InvoiceListPreview : AppCompatActivity() {
    private var invoice: InvoiceITF? = null
    private var firebaseVisionText: FirebaseVisionText? = null
    private var invoiceItemsResult: ArrayList<InvoiceItem> = ArrayList()
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

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setDataDummy() {
        val invoice1= InvoiceItem()
        invoice1.name = "ayam"
        invoice1.pricePerUnit = 1000.0

        val invoice2 = InvoiceItem()
        invoice2.name = "bebek"
        invoice2.pricePerUnit = 15000.0

        invoiceItemsResult.add(
            invoice1
        )

        invoiceItemsResult.add(
            invoice2
        )
    }

    private fun retrievInfoFromInvoiceManager() {
        invoice = InvoiceManager.invoiceOnScreen
        firebaseVisionText = InvoiceManager.firebaseVisionText
    }

    private fun processTheImage() {
        invoice?.onFinishProcessInvoice = {
            invoiceItemsResult = it
            setupRecyclerView()
            invoiceAdapter.notifyDataSetChanged()
        }

        firebaseVisionText?.let {
            invoice?.processText(it)
        }
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            invoiceAdapter = InvoicePreviewAdaptor(invoiceItemsResult)
            adapter = invoiceAdapter
        }
    }

    private fun setupButton() {
        button_calculate.setOnClickListener {
            processButton()
        }
    }

    private fun processButton() {
        Toast.makeText(this, "will process", Toast.LENGTH_LONG).show()
        val intent = Intent(this, InvoiceListResult::class.java)
        InvoiceManager.invoiceOnScreen = invoice
        InvoiceManager.firebaseVisionText = firebaseVisionText

        startActivity(intent)
    }
}