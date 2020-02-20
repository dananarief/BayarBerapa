package com.technolovy.android.bayarberapa

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.helper.roundOffDecimal
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.invoice_list_result_item.view.*
import java.text.NumberFormat
import java.util.*

class InvoiceResultAdaptor(private var invoiceItems:List<InvoiceItem>): RecyclerView.Adapter<InvoiceResultAdaptor.InvoiceHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceHolder {
        val inflatedView = parent.inflate(R.layout.invoice_list_result_item, false)
        return InvoiceHolder(inflatedView, parent.context)
    }

    override fun getItemCount(): Int {
       return invoiceItems.count()
    }

    override fun onBindViewHolder(holder: InvoiceHolder, position: Int) {
        val invoiceItem = invoiceItems[position]
        holder.bindInvoiceItem(invoiceItem)
    }

    class InvoiceHolder(private var view: View, var context: Context): RecyclerView.ViewHolder(view) {
        private var item: InvoiceItem? = null

        fun bindInvoiceItem(invoiceItem: InvoiceItem) {
            this.item = invoiceItem
            view.item_text.text = invoiceItem.name

            val roundoff = roundOffDecimal(invoiceItem.pricePerUnit)
            val pricethousand = NumberFormat.getNumberInstance(Locale.US).format(roundoff)

            view.price_text.text = pricethousand
        }
    }
}