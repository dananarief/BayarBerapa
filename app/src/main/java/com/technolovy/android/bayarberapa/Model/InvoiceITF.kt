package com.technolovy.android.bayarberapa.Model

import android.graphics.Rect
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.Serializable

// Invoice Interface
interface InvoiceITF: Serializable {
    var name: String
    var slug: String
    var invoiceItems: ArrayList<InvoiceItem>
    var firebaseText: FirebaseVisionText?
    var numOfPerson: Double
    var onFinishProcessInvoice: ((HashMap<Rect, InvoiceItem>)->Unit)?

    fun processText(firebaseText: FirebaseVisionText)
}