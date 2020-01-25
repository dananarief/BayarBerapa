package com.technolovy.android.bayarberapa.Model

import com.google.firebase.ml.vision.text.FirebaseVisionText

// Invoice Interface
interface InvoiceITF {
    var name: String
    var slug: String
    var invoiceItems: ArrayList<InvoiceItem>
    var firebaseText: FirebaseVisionText?
    var numOfPerson: Double

    fun processText(firebaseText: FirebaseVisionText)
}