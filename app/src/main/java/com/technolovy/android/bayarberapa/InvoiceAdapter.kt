package com.technolovy.android.bayarberapa

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.Helper.inflate
import com.technolovy.android.bayarberapa.Model.InvoiceItem
import kotlinx.android.synthetic.main.invoice_list_item.view.*

class InvoiceAdapter(var invoiceItems: ArrayList<InvoiceItem>): RecyclerView.Adapter<InvoiceAdapter.InvoiceHolder>(), AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("click spinner", "value ${parent?.getItemAtPosition(position)}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceHolder {
        val inflatedView = parent.inflate(R.layout.invoice_list_item, false)
        return InvoiceHolder(inflatedView, parent.context)
    }

    override fun getItemCount(): Int {
        return invoiceItems.count()
    }

    override fun onBindViewHolder(holder: InvoiceHolder, position: Int) {
        val invoiceItem = invoiceItems[position]
        holder.bindInvoiceItem(invoiceItem)
        holder.itemView.spinner_invoice_type.onItemSelectedListener = this
//        holder.itemView.spinner_invoice_type.setOnItemClickListener { parent, view, position, id ->
//            Log.d("click spinner", "${view.spinner_invoice_type.selectedItem} ${id}")
//        }

        holder.itemView.price_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("editTextprice","edit message into ${s}")
            }

        })

        holder.itemView.item_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("editTextitem","edit message into ${s}")
            }

        })
    }

    class InvoiceHolder(private var view: View, var context: Context): RecyclerView.ViewHolder(view) {
        private var item: InvoiceItem? = null
        private var spinnerAdapter: ArrayAdapter<CharSequence>? = null

        fun bindInvoiceItem(invoiceItem: InvoiceItem) {
            this.item = invoiceItem
            view.item_text.setText(invoiceItem.name)
            view.price_text.setText(invoiceItem.price.toString())

            var spinnerAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.invoice_type_array,
                R.layout.custom_spinner_item)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            view.spinner_invoice_type.adapter = spinnerAdapter
            val defaultValue = spinnerAdapter.getPosition(invoiceItem?.type?.displayName)
            Log.d("spinner", "default value ${defaultValue}")
            view.spinner_invoice_type.setSelection(defaultValue)
            Log.d("test view", "bindinv item ${view.item_text.text} ${view.price_text.text}")
        }

    }
}