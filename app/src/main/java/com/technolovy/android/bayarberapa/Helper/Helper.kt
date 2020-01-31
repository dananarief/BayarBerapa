package com.technolovy.android.bayarberapa.Helper

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

    checkText = checkText.replace("Rp", "")
    checkText = checkText.replace(".", "")
    checkText = checkText.replace(",", "")

    val price: Double? = checkText.toDoubleOrNull()

    return price
}