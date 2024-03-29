package com.technolovy.android.bayarberapa.model

import android.graphics.Rect
import android.util.Log
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.helper.extractPriceToDouble
import java.io.Serializable
import kotlin.math.abs

class Grab: InvoiceITF, Serializable {
    override var name: String = "Grab"
    override var slug: String = "grab"
    override var invoiceItems: ArrayList<InvoiceItem> = ArrayList()
    override var firebaseText: FirebaseVisionText? = null
    override var numOfPerson: Double = 1.0
    override var onFinishProcessInvoice: ((ArrayList<InvoiceItem>)->Unit)? = null

    override fun processText(firebaseText: FirebaseVisionText) {
        //decide frame scope:
        // for grab, the start of frame scope is the bottom frame of "order summary" word
        // and the bottom of frame scope is the start of "Total" word
        val frameScope: Rect = getInvoiceFrameScope(firebaseText)
        // find price text could be 10.000 or Rp 100.000 format, make the frame as key
        val invoiceItemsToProcess = createFrameKeyBasedOnPriceText(firebaseText,frameScope)

        // re looping element, fill the qty of the key object
        fillAttributesForInvoiceItems(firebaseText, frameScope, invoiceItemsToProcess)

        // re looping line replace key object's name with line version (more complete)
        fillNamesForInvoiceItems(firebaseText, frameScope, invoiceItemsToProcess)

        // re looping the map object, identify the type (purchase, discount, tax, or shared_fee)
        fillTypeForInvoiceItems(invoiceItemsToProcess)

        // improved process, eg: tax case where sometimes it is not recognized by system
        improveResult(invoiceItemsToProcess)

        // calculate
        val result = calculate(convertHashMapToList(invoiceItemsToProcess))

        onFinishProcessInvoice?.invoke(result)
    }

    private fun convertHashMapToList(hashMapData: java.util.HashMap<Rect, InvoiceItem>?): ArrayList<InvoiceItem> {
        val invoiceItemList: ArrayList<InvoiceItem> = ArrayList()
        hashMapData?.let {
            for ((_, v) in it) {
                invoiceItemList.add(v)
            }
        }
        return invoiceItemList
    }

    private fun getInvoiceFrameScope(firebaseText: FirebaseVisionText): Rect {
        val frameScope = Rect()
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {

                //make bottom frame order summary as starting point
                if (line.text.contains("order summary",ignoreCase = true)) {
                    line.boundingBox?.bottom?.let {
                        frameScope.top = it
                    }
                }

                //make top frame Total as end point
                if ((line.text.contains("total",ignoreCase = true)) && !line.text.contains("subtotal", ignoreCase = true)) {
                    line.boundingBox?.top?.let {
                        frameScope.bottom = it
                    }
                }
            }
        }
        return frameScope
    }

    fun getPurchaseItemTypeFrameScope(firebaseText: FirebaseVisionText): Rect {
        val frameScope = Rect()
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

    private fun createFrameKeyBasedOnPriceText(firebaseText: FirebaseVisionText,
                                       frameScope: Rect): HashMap<Rect, InvoiceItem> {

        val invoiceItems = hashMapOf<Rect, InvoiceItem>()
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
//                line.boundingBox?.let {
//                    val rect = it
//                    if (isFrameInsideScope(rect, frameScope)) {
//                        val priceWithDoubleFormat = extractPriceToDouble(line.text)
//                        priceWithDoubleFormat?.let { doublePrice ->
//                            //if the text is price, save the frame as key to the map
//                            invoiceItems[rect] = InvoiceItem(rect, doublePrice)
//                        }
//                    }
//                }

                for (element in line.elements) {
                    element?.boundingBox?.let {
                        val rect = it
                        if (isFrameInsideScope(rect, frameScope)) {
                            val priceWithDoubleFormat = extractPriceToDouble(element.text)
                            priceWithDoubleFormat?.let { doublePrice ->
                                //if the text is price, save the frame as key to the map
                                invoiceItems[rect] = InvoiceItem(rect, doublePrice)
                            }
                        }
                    }
                }
            }
        }


        return invoiceItems
    }

    private fun fillAttributesForInvoiceItems(firebaseText: FirebaseVisionText,
                                      frameScope: Rect,
                                      invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    //Log.d("create element", "${element.text} ${element.boundingBox}")
                    element?.boundingBox?.let {
                        val rect = it
                        if (isFrameInsideScope(rect, frameScope)) {
                            val qtyWithDoubleFormat = extractQuantityTextToDouble(element.text)
                            qtyWithDoubleFormat?.let {
                                //assign the qty for the key
                                for ((k, _) in invoiceItems) {
                                    if (isFrameConsideredAsOneLine(rect,k)) {
                                        val invoiceItem = invoiceItems[k]
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

    private fun fillNamesForInvoiceItems(firebaseText: FirebaseVisionText,
                                 frameScope: Rect,
                                 invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for (block in firebaseText.textBlocks) {
            for (line in block.lines) {
                line.boundingBox?.let {
                    val rect = it
                    //Log.d("line text","${line.text}")
                    if (isFrameInsideScope(rect, frameScope)) {
                        for ((k, _) in invoiceItems) {
                            if (isFrameConsideredAsOneLine(rect,k)) {
                                //make sure the text is not qty or price
                                if (extractPriceToDouble(line.text) == null && (extractQuantityTextToDouble(line.text) == null)) {
                                    val invoiceItem = invoiceItems[k]
                                    invoiceItem?.name = line.text
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fillTypeForInvoiceItems(invoiceItems: MutableMap<Rect, InvoiceItem>) {
        for ((k, v) in invoiceItems) {
            if (v.quantity > 0) {
                v.type = InvoiceItem.InvoiceType.PURCHASEITEM
            } else if (v.name.contains("Promo",ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.DISCOUNT
            } else if (v.name.contains("tax", ignoreCase = true)) {
                if (v.name.contains("incl")) {
                    //incl tax is already calculted into item price
                    //invoiceItems.remove(k)
                    v.name = "incl. tax (sudah dimasukkan di harga barang)"
                    v.type = InvoiceItem.InvoiceType.AlreadyTAX
                } else {
                    v.name = "additional tax (akan ditambahkan saat perhitungan)"
                    v.type = InvoiceItem.InvoiceType.TAX
                }
            } else if (v.name.contains("delivery fee", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.SHARED_FEE
            } else if (v.name.contains("charges by restaurant", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.TAX
            } else if (v.name.contains("subtotal", ignoreCase = true)) {
                v.type = InvoiceItem.InvoiceType.SUBTOTAL
            }else {
                v.type = InvoiceItem.InvoiceType.NOTRECOGNIZED
            }
        }
    }

    override fun calculate(invoiceItems: ArrayList<InvoiceItem>): ArrayList<InvoiceItem> {
        //will set invoice item pricePerUnit attribute
        var subTotal = 0.0
        var taxPercentage = ArrayList<Double>()
        var discountPercentage = 0.0

        //get info about subtotal
        for (invoiceItem in invoiceItems) {
            if (invoiceItem.name.contains("subtotal", ignoreCase = true)) {
                subTotal = invoiceItem.price
            }
        }

        //get info about tax percentage and discount percentage
        for (invoiceItem in invoiceItems) {
            if (invoiceItem.type == InvoiceItem.InvoiceType.TAX) {
                val percentage = invoiceItem.price / subTotal
                taxPercentage.add(percentage)
            }


            if (invoiceItem.type == InvoiceItem.InvoiceType.DISCOUNT) {
                discountPercentage = abs(invoiceItem.price) / subTotal
            }
        }

        //assign price per qty
        for (invoiceItem in invoiceItems) {
            if (invoiceItem.type == InvoiceItem.InvoiceType.PURCHASEITEM) {
                val pricePerQuantity = invoiceItem.price/invoiceItem.quantity

                val discountPriceperQty: Double = pricePerQuantity * discountPercentage

                var resultPricePerUnit = pricePerQuantity

                for (percentage in taxPercentage) {
                    val taxPricePerQty: Double = pricePerQuantity * percentage
                    resultPricePerUnit += taxPricePerQty
                }

                resultPricePerUnit -= discountPriceperQty

                invoiceItem.pricePerUnit = resultPricePerUnit
            }

            if (invoiceItem.type == InvoiceItem.InvoiceType.SHARED_FEE) {
                invoiceItem.pricePerUnit = invoiceItem.price/numOfPerson
            }
        }

        this.invoiceItems = invoiceItems

        for (item in this.invoiceItems ) {
            println("${item.rect} ${item.getPriceForDebug()}")
            Log.d("result","${item.rect} ${item.getPriceForDebug()} ${item.name} ${item.quantity} ${item.type} ${item.pricePerUnit}")
        }

        return invoiceItems
    }

    private fun isFrameInsideScope(frameToCheck: Rect, frameScope: Rect): Boolean {
        return (frameToCheck.top > frameScope.top) && (frameToCheck.bottom < frameScope.bottom)
    }

    //check if format is 1x, 11x which how grab invoice write quantity in their invoice
    private fun extractQuantityTextToDouble(elementText: String): Double? {
        if (elementText.contains("x", ignoreCase = true)) {
            //stripped x
            var strippedText = elementText.replace("x","")
            strippedText = strippedText.replace("X","")

            //check if text contains number only after stripped the "x"
            if (strippedText.equals("T",ignoreCase = false)) {
                strippedText = "1"
            }
            return strippedText.toDoubleOrNull()
        }

        return null
    }

    private fun isFrameConsideredAsOneLine(frame1: Rect, frame2: Rect): Boolean {
        var diffTop = frame1.top - frame2.top
        diffTop = abs(diffTop)
        return diffTop < 15
    }

    private fun improveResult(result: HashMap<Rect, InvoiceItem>) {
        autoFillTax(result)
        removeWrongKey(result)
    }

    private fun autoFillTax(result: HashMap<Rect, InvoiceItem>) {

        var missingInvoiceInfo: InvoiceItem? = null

        for ((_, v) in result) {
            if (v.name.isEmpty()) {
                missingInvoiceInfo = v
                break
            }
        }


        var isTheMissingInvoiceTax: Boolean = true
        for ((_, v) in result) {
            if (v.name.contains("subtotal", ignoreCase = true)) {
                missingInvoiceInfo?.rect?.top?.let { taxTop ->
                    v.rect?.bottom?.let { subTotalBottom ->
                        isTheMissingInvoiceTax = isTheMissingInvoiceTax && (taxTop > subTotalBottom)
                    }
                }
            } else if (v.name.contains("Promo",ignoreCase = true)) {
                missingInvoiceInfo?.rect?.bottom?.let { taxBottom ->
                    v.rect?.top?.let { promoTop ->
                        isTheMissingInvoiceTax = isTheMissingInvoiceTax && (taxBottom < promoTop)
                    }
                }
            } else if (v.name.contains("tax", ignoreCase = true)) {

            } else if (v.name.contains("delivery fee", ignoreCase = true)) {
                missingInvoiceInfo?.rect?.bottom?.let { taxBottom ->
                    v.rect?.top?.let { deliveryTop ->
                        isTheMissingInvoiceTax = isTheMissingInvoiceTax && (taxBottom < deliveryTop)
                    }
                }
            } else if (v.name.contains("charges by restaurant", ignoreCase = true)) {
                missingInvoiceInfo?.rect?.bottom?.let { taxBottom ->
                    v.rect?.top?.let { chargeServiceTop ->
                        isTheMissingInvoiceTax = isTheMissingInvoiceTax && (taxBottom < chargeServiceTop)
                    }
                }
            }
        }

        if (isTheMissingInvoiceTax) {
            missingInvoiceInfo?.name = "tax terpisah"
            missingInvoiceInfo?.type = InvoiceItem.InvoiceType.TAX
        }
    }

    //to handle something like "buy 20 get 20 10.000", and result in 3 items (10.000, 20, 20)
    private fun removeWrongKey(result: HashMap<Rect, InvoiceItem>) {
        var priceBound: Rect? = null

        for ((_, v) in result) {
            if (v.type == InvoiceItem.InvoiceType.SUBTOTAL) {
                priceBound = v.rect
                break
            }
        }


        var keyToRemove = ArrayList<Rect>()
        for ((_, v) in result) {
            v.rect?.let { itemRect ->
                priceBound?.let { boundRect ->
                    if (itemRect.right < boundRect.left) {
                        keyToRemove.add(itemRect)
                    }
                }
            }
        }

        for (k in keyToRemove) {
            result.remove(k)
        }
    }
}