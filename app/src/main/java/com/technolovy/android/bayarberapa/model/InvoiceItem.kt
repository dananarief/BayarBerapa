package com.technolovy.android.bayarberapa.model

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class InvoiceItem() : Serializable, Parcelable {
    var name: String = ""
    var quantity: Double = -1.0
    //if qty is 2, then this is price for 2 items
    // dont use price to show in display
    // use pricePerUnitInstead
    var price: Double = -1.0
    var rect: Rect? = null
    var type: InvoiceType? = null
    var pricePerUnit: Double = 0.0 //price per qty or per person (for shared fee)

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        quantity = parcel.readDouble()
        price = parcel.readDouble()
        rect = parcel.readParcelable(Rect::class.java.classLoader)
        pricePerUnit = parcel.readDouble()
        type = InvoiceType.valueOf(parcel.readString()  )
    }

    constructor(rect: Rect, price: Double) : this() {
        this.rect = rect
        this.price = price
    }

    enum class InvoiceType(val displayName: String) {
        NOTRECOGNIZED("Not Recognized"),
        PURCHASEITEM("Purchase"), //foods, drink, any item that user buy
        DISCOUNT("Discount"), //reduction for each purchase item
        TAX("Tax"), //the tax addition for each purchase item
        SHARED_FEE("Shared Fee"), // service charged by restaurant or delivery fee
        SUBTOTAL("Subtotal") // subtotal of purchase item
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(quantity)
        parcel.writeDouble(price)
        parcel.writeParcelable(rect, flags)
        parcel.writeDouble(pricePerUnit)
        parcel.writeString(this.type?.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InvoiceItem> {
        override fun createFromParcel(parcel: Parcel): InvoiceItem {
            return InvoiceItem(parcel)
        }

        override fun newArray(size: Int): Array<InvoiceItem?> {
            return arrayOfNulls(size)
        }
    }
}