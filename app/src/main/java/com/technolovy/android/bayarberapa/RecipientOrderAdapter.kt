package com.technolovy.android.bayarberapa

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.RecipientOrder
import kotlinx.android.synthetic.main.recipient_order_list_item.view.*

class RecipientOrderAdapter(private var recipientOrders:ArrayList<RecipientOrder>): RecyclerView.Adapter<RecipientOrderAdapter.RecipientOrderHolder>() {
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
    }

    class RecipientOrderHolder(private var view: View): RecyclerView.ViewHolder(view) {
        var item: RecipientOrder? = null

        fun bindRecipientOrder(recipientOrder: RecipientOrder) {
            this.item = recipientOrder
            view.item_name.text = recipientOrder.invoiceItem?.name
            view.qty_text.text = recipientOrder.buyQty.toString()

            view.minus_button.setOnClickListener {
                val qtyWillShow = view.qty_text.text.toString().toDouble() - 1
                if (qtyWillShow >= 0) {
                    view.qty_text.text = qtyWillShow.toString()
                    recipientOrder.buyQty  = qtyWillShow
                }
            }

            view.plus_button.setOnClickListener {
                val qtyWillShow = view.qty_text.text.toString().toDouble() + 1
                var qtyMaxLimit = item?.invoiceItem?.quantity
                if (item?.invoiceItem?.type == InvoiceItem.InvoiceType.SHARED_FEE) {
                    qtyMaxLimit = 1.0
                }
                qtyMaxLimit?.let {
                    if (qtyWillShow <= it) {
                        Log.d("change qty", "${qtyWillShow} ${view.qty_text.text}")
                        view.qty_text.text = qtyWillShow.toString()
                        recipientOrder.buyQty  = qtyWillShow
                    }
                }
            }
        }
    }
}