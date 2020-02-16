package com.technolovy.android.bayarberapa

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import com.technolovy.android.bayarberapa.model.RecipientOrder
import kotlinx.android.synthetic.main.activity_add_recepient_form.*
import kotlinx.android.synthetic.main.activity_add_recepient_form.recycler_view

class AddRecepientForm : AppCompatActivity() {
    private var recipient: Recipient? = null
    private lateinit var recipientOrderAdapter: RecipientOrderAdapter
    private var editPosition: Int = -1
    private var privateMode = 0
    private var prefName = "bayarBerapa"
    var cache: SharedPreferences? = null
    var sugestion = ArrayList<String>()
    var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cache = getSharedPreferences(prefName, privateMode)
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
        //get suggestion from cache
        val nameSugestions = cache?.getStringSet("nameSugestion",null)
        nameSugestions?.let {
            sugestion = ArrayList(it)
        }

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
                    if (isEditMode) {
                        InvoiceManager.recipientList[editPosition] = it
                    } else {
                        InvoiceManager.recipientList.add(it)
                    }
                }
                saveNameToCache()
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

    private fun saveNameToCache() {
        cache?.let {
            val editor = it.edit()
            recipient?.name?.let {
                sugestion.add(it)
            }
            val suggestionInSetString = HashSet(sugestion)
            editor.putStringSet("nameSugestion",suggestionInSetString)
            editor.apply()
        }
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
            val savedRecipient: Recipient? = InvoiceManager.recipientList.getOrNull(editPosition)
            savedRecipient?.let {
                recipient = it
                isEditMode = true
            }
        }
    }
}
