package com.technolovy.android.bayarberapa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import kotlinx.android.synthetic.main.activity_add_recepient_form.*
import kotlinx.android.synthetic.main.activity_add_recepient_form.recycler_view
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*
import kotlinx.android.synthetic.main.activity_tag_people.*

class AddRecepientForm : AppCompatActivity() {
    private lateinit var recipientOrderAdapter: RecipientOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recepient_form)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupAutoCompleteTextView()
        setupRecylerView()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAutoCompleteTextView() {
        var sugestion = ArrayList<String>()
        sugestion.add("aldo")
        sugestion.add("anis")
        var adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,sugestion)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun setupRecylerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            var dummyRecipient = Recipient()
            dummyRecipient.name = "ikhsan"
            var invoiceItem = InvoiceItem()
            invoiceItem.quantity = 1.0
            invoiceItem.name = "aqua"
            invoiceItem.pricePerUnit = 1500.0
            invoiceItem.price = 1500.0
            invoiceItem.type = InvoiceItem.InvoiceType.PURCHASEITEM
            var dummyInvoiceListItem = ArrayList<InvoiceItem>()
            dummyInvoiceListItem.add(invoiceItem)
            dummyRecipient.invoiceItems = dummyInvoiceListItem

            var dummyRecipientList = ArrayList<Recipient>()
            dummyRecipientList.add(dummyRecipient)

            recipientOrderAdapter = RecipientOrderAdapter(dummyInvoiceListItem)
            recycler_view.let {
                adapter = recipientOrderAdapter
            }
        }
    }
}
