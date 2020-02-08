package com.technolovy.android.bayarberapa.model

class Recipient {
    var name: String = ""
    var invoiceItems: ArrayList<InvoiceItem>? = null

    fun getTotalPrice(): Double {
        var price = 0.0
        invoiceItems?.let {
            for (item in it) {
                price += (item.pricePerUnit * item.quantity)
            }
        }
        return price
    }
}