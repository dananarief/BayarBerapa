package com.technolovy.android.bayarberapa

import android.content.Context
import android.icu.text.DecimalFormat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.extractPriceToDouble
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.helper.rupiah
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.custom_spinner_item.view.*
import kotlinx.android.synthetic.main.custom_spinner_item_error.view.*
import kotlinx.android.synthetic.main.invoice_list_preview_item.view.*
import kotlin.contracts.contract

class InvoicePreviewAdaptor(private var invoiceItems: ArrayList<InvoiceItem>): RecyclerView.Adapter<InvoicePreviewAdaptor.InvoiceHolder>() {
    var onTypeChange: ((Int,InvoiceItem.InvoiceType)->Unit)? = null

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

                if (invoiceItem.type == InvoiceItem.InvoiceType.NOTRECOGNIZED) {
                    view?.spinner_item?.setTextColor(ContextCompat.getColor(holder.context,R.color.colorAccent))
                } else {
                    view?.spinner_item?.setTextColor(ContextCompat.getColor(holder.context,R.color.colorPrimary))
                }

                //(parent?.getChildAt(position) as TextView).setTextColor(ContextCompat.getColor(holder.context,R.color.colorDisabled))
                //view.spinner_invoice_type.spinner_item.setTextColor(ContextCompat.getColor(holder.,R.color.colorDisabled))
                //holder.spinnerAdapter.st
//                if (enumBasedOnDropdown == InvoiceItem.InvoiceType.NOTRECOGNIZED) {
//                    holder.bindInvoiceItem(invoiceItem)
//                }
            }
        }
    }

    class InvoiceHolder(private var view: View, var context: Context): RecyclerView.ViewHolder(view) {
        private var item: InvoiceItem? = null
        var spinnerAdapter: ArrayAdapter<CharSequence>? = null

        fun bindInvoiceItem(invoiceItem: InvoiceItem) {
            this.item = invoiceItem
            view.item_text.setText(invoiceItem.name)

            val format = java.text.DecimalFormat("0.#")
            val priceWithoutDecimal = format.format(invoiceItem.price)

            view.price_text.setText(priceWithoutDecimal)


            Log.d("change","name ${invoiceItem.name}")
            Log.d("change","price ${invoiceItem.price.toString()}")
            Log.d("change","name show ${view.item_text.text}")
            Log.d("change","price show ${view.price_text.text}")

            var resourceLayout: Int = R.layout.custom_spinner_item
            if (invoiceItem.type == InvoiceItem.InvoiceType.NOTRECOGNIZED) {
                //resourceLayout = R.layout.custom_spinner_item_error
            }
            spinnerAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.invoice_type_array,
                resourceLayout)
            spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            view.spinner_invoice_type.adapter = spinnerAdapter
            val defaultValue = spinnerAdapter?.getPosition(invoiceItem.type?.displayName)
            defaultValue?.let {
                Log.d("lala default value", "${it}")
                view.spinner_invoice_type.setSelection(defaultValue)
            }
            //view.spinner_item.setText("testing")
            //view.spinner_invoice_type.spinner_item_error.setTextColor(ContextCompat.getColor(context, R.color.colorDisabled))


            view.item_text.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    Log.d("otc","after ${s.toString()}")
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    Log.d("otc","before ${s.toString()}")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d("otc"," original ${invoiceItem.name} ${s.toString()} ${start} ${before} ${count}")
                    item?.name = s.toString()
                }

            })


            view.price_text.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val priceInDouble = extractPriceToDouble(s.toString())
                    priceInDouble?.let {
                        item?.price = it
                    }
                }

            })
        }
    }
}