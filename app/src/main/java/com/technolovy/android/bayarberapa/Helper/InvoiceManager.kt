package com.technolovy.android.bayarberapa.Helper

import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.Model.InvoiceITF

object InvoiceManager {
    var invoiceOnScreen: InvoiceITF? = null
    var firebaseVisionText: FirebaseVisionText? = null
}