package com.technolovy.android.bayarberapa

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import kotlinx.android.synthetic.main.invoice_list_preview_item.view.*
import kotlinx.android.synthetic.main.invoice_list_result_item.view.price_text
import kotlinx.android.synthetic.main.people_list_item.view.*
import kotlinx.android.synthetic.main.recipient_order_list_item.view.*

class RecipientOrderAdapter(private var recipientOrders:List<InvoiceItem>): RecyclerView.Adapter<RecipientOrderAdapter.RecipientOrderHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientOrderHolder {
        val inflatedView = parent.inflate(R.layout.recipient_order_list_item, false)
        return RecipientOrderHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return recipientOrders.count()
    }

    override fun onBindViewHolder(holder: RecipientOrderHolder, position: Int) {
        val recipientOrder = recipientOrders[position]
        holder.bindRecipientOrder(recipientOrder)
        holder.itemView.qty_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("qty info", "ontextchanged")
                recipientOrder.quantity = s.toString().toDouble()
            }

        })
        holder.itemView.minus_button.setOnClickListener {
            val qtyWillShow = holder.itemView.qty_text.text.toString().toDouble() - 1
            if (qtyWillShow > 0) {
                holder.itemView.qty_text.text = qtyWillShow.toString()
            }
        }
        holder.itemView.plus_button.setOnClickListener {
            val qtyWillShow = holder.itemView.qty_text.text.toString().toDouble() + 1
            holder.itemView.qty_text.text = qtyWillShow.toString()
        }
    }

    class RecipientOrderHolder(private var view: View): RecyclerView.ViewHolder(view) {
        private var item: InvoiceItem? = null

        fun bindRecipientOrder(recipientOrder: InvoiceItem) {
            this.item = recipientOrder
            view.item_name.text = recipientOrder.name
            view.qty_text.text = "0"
        }
    }
}