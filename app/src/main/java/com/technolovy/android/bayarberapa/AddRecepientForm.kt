package com.technolovy.android.bayarberapa

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceITF
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import com.technolovy.android.bayarberapa.model.RecipientOrder
import kotlinx.android.synthetic.main.activity_add_recepient_form.*
import kotlinx.android.synthetic.main.activity_add_recepient_form.recycler_view
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*
import kotlinx.android.synthetic.main.activity_tag_people.*

class AddRecepientForm : AppCompatActivity() {
    private var recipient: Recipient? = null
    private lateinit var recipientOrderAdapter: RecipientOrderAdapter
    private var editPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recepient_form)
        retrievInfoFromInvoiceManager()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupAutoCompleteTextView()
        setupRecylerView()
        setupButton()
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
        recipient?.name?.let {
            autoCompleteTextView.setText(it)
        }
    }

    private fun setupRecylerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            recipient?.let {
                it.recipientOrders?.let {
                    recipientOrderAdapter = RecipientOrderAdapter(it)
                    recycler_view.let {
                        adapter = recipientOrderAdapter
                    }
                }
            }
        }
    }

    private fun setupButton() {
        button_add_recipient.setOnClickListener {
            if (isFormValid()) {
                recipient?.name = autoCompleteTextView.text.toString()
                recipient?.let {
                    InvoiceManager.recipientList.add(it)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun isFormValid(): Boolean {
        if (autoCompleteTextView.text.isEmpty()) {
            return false
        }

        if (recipient == null) {
            return false
        }

        return true
    }

    private fun retrievInfoFromInvoiceManager() {
        val invoice = InvoiceManager.invoiceOnScreen
        invoice?.invoiceItems?.let {
            val filteredInvoiceItems = it.filter {
                it.type == InvoiceItem.InvoiceType.PURCHASEITEM
                        || it.type == InvoiceItem.InvoiceType.SHARED_FEE
            }

            val recipientOrderList = ArrayList<RecipientOrder>()
            for (invoiceItem in filteredInvoiceItems) {
                recipientOrderList.add(RecipientOrder(invoiceItem = invoiceItem))
            }
            recipient = Recipient()
            recipient?.let {
                it.recipientOrders = recipientOrderList
            }


            // change initial value fron previous data
            editPosition = intent.getIntExtra("editId",-1)
            Log.d("editPosition", "add ${editPosition.toString()}")
            val savedRecipient: Recipient? = InvoiceManager.recipientList.getOrNull(editPosition)
            savedRecipient?.let {
                recipient = it
                Log.d("editPosition", "assigned ${it.name} ${it.getTotalPrice()}")
            }
            Log.d("editPosition", "finished")
        }
    }
}
