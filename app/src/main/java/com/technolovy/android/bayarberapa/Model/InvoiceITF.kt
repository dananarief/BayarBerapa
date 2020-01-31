package com.technolovy.android.bayarberapa.Model

import android.graphics.Rect
import com.google.firebase.ml.vision.text.FirebaseVisionText

// Invoice Interface
interface InvoiceITF {
    var name: String
    var slug: String
    var invoiceItems: ArrayList<InvoiceItem>
    var firebaseText: FirebaseVisionText?
    var numOfPerson: Double
    var onFinishProcessInvoice: ((MutableMap<Rect, InvoiceItem>)->Unit)?

    fun processText(firebaseText: FirebaseVisionText)
}