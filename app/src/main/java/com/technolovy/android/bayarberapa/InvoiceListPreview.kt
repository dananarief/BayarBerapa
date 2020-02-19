package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
        setupCheckImageText()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.preview_page_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (item.itemId == R.id.preview_page_more_menu) {
                val intent = Intent(this, PreviewInfoActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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

        button_add_invoice_item.setOnClickListener {
            val newItem = InvoiceItem()
            newItem.quantity = 1.0
            newItem.price = 0.0
            newItem.type = InvoiceItem.InvoiceType.PURCHASEITEM
            invoiceItemsResult.add(0, newItem)
            recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    private fun setupCheckImageText() {
        picture_text.setOnClickListener {
            val intent = Intent(this, ImageDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun processButton() {
        if (isListValid()) {
            val intent = Intent(this, InvoiceListResult::class.java)
            InvoiceManager.invoiceOnScreen = invoice
            InvoiceManager.firebaseVisionText = firebaseVisionText

            startActivity(intent)
        }
    }

    private fun isListValid(): Boolean {
        var isValid: Boolean = false
        invoice?.invoiceItems?.let {
            isValid = true
            for (item in it) {
                if (item.name.isEmpty()) {
                    Log.d("valid","ada yang gak valid nama")
                    Toast.makeText(this, "Terdapat nama item yang kosong", Toast.LENGTH_LONG).show()
                    return false
                }

                if (item.type == InvoiceItem.InvoiceType.NOTRECOGNIZED) {
                    Log.d("valid","ada yang gak valid tipe")
                    Toast.makeText(this, "Terdapat tipe item yang kosong", Toast.LENGTH_LONG).show()
                    return false
                }
            }
        }
        return isValid
    }
}
