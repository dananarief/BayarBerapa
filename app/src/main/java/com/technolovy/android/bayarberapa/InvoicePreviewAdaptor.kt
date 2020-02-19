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
import com.technolovy.android.bayarberapa.helper.TrackerEvent
import com.technolovy.android.bayarberapa.helper.extractPriceToDouble
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.helper.sendTracker
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
        holder.itemView.delete_button.setOnClickListener {
            sendTracker(TrackerEvent.deleteItemonPreviewPage, holder.context)
            invoiceItems.removeAt(position)
            notifyDataSetChanged()
        }


        holder.itemView.spinner_invoice_type.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sendTracker(TrackerEvent.changeTypeOnPreviewPage, holder.context)
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

                holder.itemView.qty?.isEnabled = invoiceItem.type == InvoiceItem.InvoiceType.PURCHASEITEM
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

            val qtyWithoutDecimal = format.format(invoiceItem.quantity)
            view.qty.setText(qtyWithoutDecimal)

            view.qty.isEnabled = invoiceItem.type == InvoiceItem.InvoiceType.PURCHASEITEM

            var resourceLayout: Int = R.layout.custom_spinner_item

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
                    sendTracker(TrackerEvent.changeItemNameOnPreviewPage, context)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item?.name = s.toString()
                }

            })


            view.price_text.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sendTracker(TrackerEvent.changePriceTextOnPreviewPage, context)
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

            view.qty.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sendTracker(TrackerEvent.changeQtyOnPreviewPage, context)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val newQty = s.toString().toDoubleOrNull()
                    newQty?.let {
                        item?.quantity = it
                    }
                }
            })
        }
    }
}