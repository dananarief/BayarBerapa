package com.technolovy.android.bayarberapa

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.Helper.inflate
import com.technolovy.android.bayarberapa.model.InvoiceItem
import kotlinx.android.synthetic.main.invoice_list_result_item.view.*

class InvoiceResultAdaptor(var invoiceItems:ArrayList<InvoiceItem>): RecyclerView.Adapter<InvoiceResultAdaptor.InvoiceHolder>() {
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
            view.price_text.text = invoiceItem.pricePerUnit.toString()
        }
    }
}