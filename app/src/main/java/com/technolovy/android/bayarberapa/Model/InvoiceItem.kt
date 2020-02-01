package com.technolovy.android.bayarberapa.Model

import android.graphics.Rect

class InvoiceItem {
    var name: String = ""
    var quantity: Double = -1.0
    //if qty is 2, then this is price for 2 items
    // dont use price to show in display
    // use pricePerUnitInstead
    var price: Double = -1.0
    var rect: Rect? = null
    var type: InvoiceType? = null
    var pricePerUnit: Double = 0.0 //price per qty or per person (for shared fee)

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

    fun getPriceForDebug(): Double {
        return price
    }

    // get original price
    // not the total price that is caused by qty or shared_fee
    fun getPricePerUnit(person: Double, discountPercentage: Double, taxPercentage: Double): Double {
        if (type == InvoiceType.PURCHASEITEM) {
            val pricePerQuantity = price/quantity

            val discountPriceperQty: Double = pricePerQuantity * discountPercentage

            val taxPricePerQty: Double = pricePerQuantity * taxPercentage

            var resultPricePerUnit = pricePerQuantity
            resultPricePerUnit += taxPricePerQty
            resultPricePerUnit -= discountPriceperQty

            return resultPricePerUnit
        }

        if (type == InvoiceType.DISCOUNT) {
            // discount type is not supposed to be getPricePerUnit
            return price
        }

        if (type == InvoiceType.TAX) {
            // tax type is not supposed to be getPricePerUnit
            return price
        }

        if (type == InvoiceType.SHARED_FEE) {
            return price/person
        }

        return 0.0
    }
}