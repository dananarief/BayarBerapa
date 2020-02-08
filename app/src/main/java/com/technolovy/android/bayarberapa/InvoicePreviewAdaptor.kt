package com.technolovy.android.bayarberapa

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.extractPriceToDouble
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.invoice_list_preview_item.view.*

class InvoicePreviewAdaptor(private var invoiceItems: ArrayList<InvoiceItem>): RecyclerView.Adapter<InvoicePreviewAdaptor.InvoiceHolder>(), AdapterView.OnItemSelectedListener {
    var onTypeChange: ((Int,InvoiceItem.InvoiceType)->Unit)? = null
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceHolder {
        val inflatedView = parent.inflate(R.layout.invoice_list_preview_item, false)
        return InvoiceHolder(inflatedView, parent.context)
    }

    override fun getItemCount(): Int {
        return invoiceItems.count()
    }

    override fun onBindViewHolder(holder: InvoiceHolder, position: Int) {
        val invoiceItem = invoiceItems[position]
        holder.bindInvoiceItem(invoiceItem)
        holder.itemView.spinner_invoice_type.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedDropdown = parent?.getItemAtPosition(position).toString()
                val enumBasedOnDropdown = InvoiceItem.InvoiceType.values().firstOrNull {
                    it.displayName == selectedDropdown
                }
                invoiceItem.type = enumBasedOnDropdown
                enumBasedOnDropdown?.let {
                    onTypeChange?.invoke(position,enumBasedOnDropdown)
                }
            }
        }

        holder.itemView.price_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val priceInDouble = extractPriceToDouble(s.toString())
                priceInDouble?.let {
                    invoiceItem.price = it
                }
            }

        })

        holder.itemView.item_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                invoiceItem.name = s.toString()
            }

        })
    }

    class InvoiceHolder(private var view: View, private var context: Context): RecyclerView.ViewHolder(view) {
        private var item: InvoiceItem? = null

        fun bindInvoiceItem(invoiceItem: InvoiceItem) {
            this.item = invoiceItem
            view.item_text.setText(invoiceItem.name)
            view.price_text.setText(invoiceItem.price.toString())

            val spinnerAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.invoice_type_array,
                R.layout.custom_spinner_item)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            view.spinner_invoice_type.adapter = spinnerAdapter
            val defaultValue = spinnerAdapter.getPosition(invoiceItem.type?.displayName)
            view.spinner_invoice_type.setSelection(defaultValue)
        }

    }
}