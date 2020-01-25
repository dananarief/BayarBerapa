package com.technolovy.android.bayarberapa.Model

import android.graphics.Point
import android.graphics.Rect

class InvoiceItem {
    var name: String = ""
    var quantity: Double = -1.0
    var price: Double = -1.0
    var rect: Rect? = null
    var type: InvoiceType? = null

    constructor(rect: Rect, price: Double) {
        this.rect = rect
        this.price = price
    }

    enum class InvoiceType {
        PURCHASEITEM, //foods, drink, any item that user buy
        DISCOUNT, //reduction for each purchase item
        TAX, //the tax addition for each purchase item
        SHARED_FEE // service charged by restaurant or delivery fee
    }
}