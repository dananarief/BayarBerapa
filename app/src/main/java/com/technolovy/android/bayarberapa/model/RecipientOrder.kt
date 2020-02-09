package com.technolovy.android.bayarberapa.model

class RecipientOrder {
    var name: String = ""
    var invoiceItem: InvoiceItem? = null
    var buyQty: Double = 0.0

    constructor(invoiceItem: InvoiceItem) {
        this.invoiceItem = invoiceItem
    }
}