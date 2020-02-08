package com.technolovy.android.bayarberapa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import kotlinx.android.synthetic.main.activity_invoice_list_result.*
import kotlinx.android.synthetic.main.activity_tag_people.*
import kotlinx.android.synthetic.main.activity_tag_people.recycler_view

class TagPeople : AppCompatActivity() {
    private lateinit var recipientAdapter: TagPeopleListAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_people)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()
        setupButton()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
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

            recipientAdapter = TagPeopleListAdaptor(dummyRecipientList)
            recycler_view.let {
                adapter = recipientAdapter
            }
        }
    }

    private fun setupButton() {
        button_add_tag_people.setOnClickListener {
            val intent = Intent(this, AddRecepientForm::class.java)
            startActivity(intent)
        }
    }
}
