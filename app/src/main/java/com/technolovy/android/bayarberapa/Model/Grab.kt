package com.technolovy.android.bayarberapa.Model

import android.graphics.Rect
import android.util.Log
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.Helper.extractPriceToDouble
import com.technolovy.android.bayarberapa.Helper.isPrice

class Grab: InvoiceITF {

    override var name: String = "Grab"
    override var slug: String = "grab"
    override var invoiceItems: ArrayList<InvoiceItem> = arrayListOf<InvoiceItem>()
    override var firebaseText: FirebaseVisionText? = null
    override var numOfPerson: Double = 1.0
    override var onFinishProcessInvoice: ((MutableMap<Rect, InvoiceItem>)->Unit)? = null

    override fun processText(firebaseText: FirebaseVisionText) {
        //decide frame scope:
        // for grab, the start of frame scope is the bottom frame of "order summary" word
        // and the bottom of frame scope is the start of "Total" word
        val frameScope: Rect = getInvoiceFrameScope(firebaseText)
        // find price text could be 10.000 or Rp 100.000 format, make the frame as key
        val invoiceItems = createFrameKeyBasedOnPriceText(firebaseText,frameScope)

        // re looping element, fill the qty of the key object
        fillAttributesForInvoiceItems(firebaseText, frameScope, invoiceItems)

        // re looping line replace key object's name with line version (more complete)
        fillNamesForInvoiceItems(firebaseText, frameScope, invoiceItems)

        // re looping the map object, identify the type (purchase, discount, tax, or shared_fee)
        fillTypeForInvoiceItems(invoiceItems)

        // calculate
        calculate(invoiceItems)

        //print temp result, should be deleted once feature is complete
        for ((k, v) in invoiceItems) {
            println("$k = ${v.rect} ${v.getPriceForDebug()}")
            Log.d("result","$k = ${v.rect} ${v.getPriceForDebug()} ${v.name} ${v.quantity} ${v.type} ${v.pricePerUnit}")
        }

       onFinishProcessInvoice?.invoke(invoiceItems)
    }

    fun getInvoiceFrameScope(firebaseText: FirebaseVisionText): Rect {
        var frameScope: Rect = Rect()
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {

                //make bottom frame order summary as starting point
                if (line.text.equals("order summary", ignoreCase = true)) {
                    line.boundingBox?.bottom?.let {
                        frameScope.top = it
                    }
                }

                //make top frame Total as end point
                if (line.text.equals("total", ignoreCase = true)) {
                    line.boundingBox?.top?.let {
                        frameScope.bottom = it
                    }
                }
            }
        }

        return frameScope
    }


    fun getPurchaseItemTypeFrameScope(firebaseText: FirebaseVisionText): Rect {
        var frameScope: Rect = Rect()
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {

                //make bottom frame order summary as starting point
                if (line.text.equals("order summary", ignoreCase = true)) {
                    line.boundingBox?.bottom?.let {
                        frameScope.top = it
                    }
                }

                //make top frame Total as end point
                if (line.text.equals("subtotal", ignoreCase = true)) {
                    line.boundingBox?.top?.let {
                        frameScope.bottom = it
                    }
                }
            }
        }

        return frameScope
    }


    fun createFrameKeyBasedOnPriceText(firebaseText: FirebaseVisionText,
                                       frameScope: Rect): MutableMap<Rect, InvoiceItem> {
        var invoiceItems = mutableMapOf<Rect, InvoiceItem>()
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    element?.boundingBox?.let {
                        var rect = it
                        if (isFrameInsideScope(rect, frameScope)) {
                            var priceWithDoubleFormat = extractPriceToDouble(element.text)
                            priceWithDoubleFormat?.let {
                                //if the text is price, save the frame as key to the map
                                var price = it
                                invoiceItems[rect] = InvoiceItem(rect, price)
                            }
                        }
                    }
                }
            }
        }
        return invoiceItems
    }

    fun fillAttributesForInvoiceItems(firebaseText: FirebaseVisionText,
                                      frameScope: Rect,
                                      invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    element?.boundingBox?.let {
                        var rect = it
                        if (isFrameInsideScope(rect, frameScope)) {
                            var qtyWithDoubleFormat = extractQuantityTextToDouble(element.text)
                            qtyWithDoubleFormat?.let {
                                //assign the qty for the key
                                for ((k, v) in invoiceItems) {
                                    if (isFrameConsideredAsOneLine(rect,k)) {
                                        var invoiceItem = invoiceItems[k]
                                        invoiceItem?.quantity = qtyWithDoubleFormat
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun fillNamesForInvoiceItems(firebaseText: FirebaseVisionText,
                                 frameScope: Rect,
                                 invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
                line.boundingBox?.let {
                    var rect = it

                    if (isFrameInsideScope(rect, frameScope)) {
                        for ((k, v) in invoiceItems) {
                            if (isFrameConsideredAsOneLine(rect,k)) {
                                //make sure the text is not qty or price
                                if (extractPriceToDouble(line.text) == null && (extractQuantityTextToDouble(line.text) == null)) {
                                    var invoiceItem = invoiceItems[k]
                                    invoiceItem?.name = line.text
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun fillTypeForInvoiceItems(invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for ((k, v) in invoiceItems) {
            if (v.quantity > 0) {
                v.type = InvoiceItem.InvoiceType.PURCHASEITEM
            } else if (v.name.contains("Promo",ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.DISCOUNT
            } else if (v.name.contains("tax", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.TAX
            } else if (v.name.contains("delivery fee", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.SHARED_FEE
            } else if (v.name.contains("charges by restaurant", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.SHARED_FEE
            }
        }
    }

    fun calculate(invoiceItems: MutableMap<Rect, InvoiceItem>) {
        //will set invoice item pricePerUnit attribute
        var subTotal: Double = 0.0
        var taxPercentage: Double = 0.0
        var discountPercentage: Double = 0.0

        //get info about subtotal
        for ((k, v) in invoiceItems) {
            if (v.name.contains("subtotal", ignoreCase = true)) {
                subTotal = v.price
            }
        }

        //get info about tax percentage and discount percentage
        for ((k, v) in invoiceItems) {
            if (v.type == InvoiceItem.InvoiceType.TAX) {
                taxPercentage = v.price / subTotal
            }

            if (v.type == InvoiceItem.InvoiceType.DISCOUNT) {
                discountPercentage = Math.abs(v.price) / subTotal
            }
        }

        //assign price per qty
        for ((k, v) in invoiceItems) {
            if (v.type == InvoiceItem.InvoiceType.PURCHASEITEM) {
                val pricePerQuantity = v.price/v.quantity

                val discountPriceperQty: Double = pricePerQuantity * discountPercentage

                val taxPricePerQty: Double = pricePerQuantity * taxPercentage

                var resultPricePerUnit = pricePerQuantity
                resultPricePerUnit += taxPricePerQty
                resultPricePerUnit -= discountPriceperQty

                v.pricePerUnit = resultPricePerUnit
            }

            if (v.type == InvoiceItem.InvoiceType.SHARED_FEE) {
                v.pricePerUnit = v.price/numOfPerson
            }
        }
    }

    fun isFrameInsideScope(frameToCheck: Rect, frameScope: Rect): Boolean {
        return (frameToCheck.top > frameScope.top) && (frameToCheck.bottom < frameScope.bottom)
    }

    //check if format is 1x, 11x which how grab invoice write quantity in their invoice
    fun extractQuantityTextToDouble(elementText: String): Double? {
        if (elementText.contains("x", ignoreCase = true)) {
            //stripped x
            var strippedText = elementText.replace("x","")
            strippedText = strippedText.replace("X","")

            //check if text contains number only after stripped the "x"
            return strippedText.toDoubleOrNull()
        }

        return null
    }

    fun isFrameConsideredAsOneLine(frame1: Rect, frame2: Rect): Boolean {
        var diffTop = frame1.top - frame2.top
        diffTop = Math.abs(diffTop)
        return diffTop < 15
    }
}