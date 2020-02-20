package com.technolovy.android.bayarberapa.helper

import android.net.Uri
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.technolovy.android.bayarberapa.model.InvoiceITF
import com.technolovy.android.bayarberapa.model.Recipient

object InvoiceManager {
    var invoiceOnScreen: InvoiceITF? = null
    var firebaseVisionText: FirebaseVisionText? = null
    var recipientList = ArrayList<Recipient>()
    var invoiceImg: Uri? = null
    var journeyId: String = ""
}