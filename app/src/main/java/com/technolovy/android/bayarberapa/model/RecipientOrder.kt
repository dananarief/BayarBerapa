package com.technolovy.android.bayarberapa.model

class RecipientOrder {
    var name: String = ""
    var invoiceItem: InvoiceItem? = null
    var buyQty: Double = 0.0

    constructor(invoiceItem: InvoiceItem) {
        this.invoiceItem = invoiceItem
    }

    fun createCopy(): RecipientOrder {
        invoiceItem?.let {
            val copy = RecipientOrder(invoiceItem = it.createCopy())
            copy.name = name
            copy.invoiceItem = it.createCopy()
            copy.buyQty = buyQty
            return copy
        }

        //todo: at tracker firebase to this error
        return this
    }
}