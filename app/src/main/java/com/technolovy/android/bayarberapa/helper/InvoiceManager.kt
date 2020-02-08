package com.technolovy.android.bayarberapa.helper

import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.model.InvoiceITF

object InvoiceManager {
    var invoiceOnScreen: InvoiceITF? = null
    var firebaseVisionText: FirebaseVisionText? = null
}