package com.technolovy.android.bayarberapa.model

class Recipient {
    var name: String = ""
    var recipientOrders: ArrayList<RecipientOrder>? = null

    fun getTotalPrice(): Double {
        var price = 0.0
        recipientOrders?.let {
            for (item in it) {
                item.invoiceItem?.let {
                    price += (it.pricePerUnit * item.buyQty)
                }
            }
        }
        return price
    }
}