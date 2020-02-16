package com.technolovy.android.bayarberapa

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technolovy.android.bayarberapa.helper.inflate
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import kotlinx.android.synthetic.main.invoice_list_result_item.view.price_text
import kotlinx.android.synthetic.main.people_list_item.view.*

class TagPeopleListAdaptor(private var recipients:List<Recipient>): RecyclerView.Adapter<TagPeopleListAdaptor.TagPeopleListHolder>() {
    var onTapEdit: ((Int)->Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagPeopleListHolder {
        val inflatedView = parent.inflate(R.layout.people_list_item, false)
        return TagPeopleListHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return recipients.count()
    }

    override fun onBindViewHolder(holder: TagPeopleListHolder, position: Int) {
        val recipients = recipients[position]
        holder.bindRecipientItem(recipients)
        holder.itemView.edit_text.setOnClickListener {
            onTapEdit?.invoke(position)
        }
    }

    class TagPeopleListHolder(private var view: View): RecyclerView.ViewHolder(view) {
        private var item: Recipient? = null

        fun bindRecipientItem(recipients: Recipient) {
            this.item = recipients
            view.people_name.text = recipients.name
            view.price_text.text = recipients.getTotalPrice().toString()
        }
    }
}