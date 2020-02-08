package com.technolovy.android.bayarberapa.helper

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

///*
// Check if text is price
// eg: Rp.15.000 -> true
// eg: 11.000 -> true
// eg: 16 -> true
// eg: AARp16.000 -> false
// */
fun isPrice(text: String): Boolean {
    var checkText = text


    if (checkText.contains("Rp")) {
        checkText = checkText.replace("Rp", "")
        checkText = checkText.replace(".", "")
        checkText = checkText.replace(",", "")

        val priceFloat = checkText.toFloatOrNull()

        if (priceFloat != null) {
            return true
        }
    }

    return false
}

fun extractPriceToDouble(text: String): Double? {
    var checkText = text
    Log.d("check price","${checkText}")
    checkText = checkText.replace("Rp", "")
    checkText = checkText.replace(".", "")
    checkText = checkText.replace(",", "")

    val price: Double? = checkText.toDoubleOrNull()

    return price
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
