package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_list_result)

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

    private fun processTheImage() {
        Log.d("test view","start")
        invoice?.invoiceItems?.let {
            invoice?.calculate(it)
            setupRecyclerView()
            invoiceAdapter.notifyDataSetChanged()
            isResultLoading = false
            render()
            Log.d("test view","done calculate")
        }
    }

    private fun setupRecyclerView() {
        Log.d("test view","setup recyclerview")
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
            sendTracker(TrackerEvent.tagFriendsOnResultPage, this)
            val intent = Intent(this, TagPeople::class.java)
            InvoiceManager.invoiceOnScreen = invoice
            startActivity(intent)
        }
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
