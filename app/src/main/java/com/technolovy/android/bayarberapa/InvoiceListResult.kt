package com.technolovy.android.bayarberapa

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.Helper.InvoiceManager
import com.technolovy.android.bayarberapa.Model.InvoiceITF
import com.technolovy.android.bayarberapa.Model.InvoiceItem
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*
import java.util.HashMap

class InvoiceListResult : AppCompatActivity() {

    var invoice: InvoiceITF? = null
    private lateinit var invoiceAdapter: InvoiceResultAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_list_result)

        setupRecyclerView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        retrievInfoFromInvoiceManager()
        processTheImage()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun retrievInfoFromInvoiceManager() {
        invoice = InvoiceManager.invoiceOnScreen
        Log.d("pageinvoice","count ${invoice?.numOfPerson}")
    }

    fun processTheImage() {
        Log.d("test view","start")
        invoice?.invoiceItems?.let {
            invoice?.calculate(it)
            setupRecyclerView()
            invoiceAdapter.notifyDataSetChanged()
            Log.d("test view","done calculate")
        }
    }

    fun setupRecyclerView() {
        Log.d("test view","setup recyclerview")
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            invoice?.invoiceItems?.let {
                invoiceAdapter = InvoiceResultAdaptor(it)
                adapter = invoiceAdapter
            }
        }
    }
}
