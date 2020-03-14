package com.technolovy.android.bayarberapa.helper

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.firebase.analytics.FirebaseAnalytics
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

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
    checkText = checkText.replace("o", "0")
    checkText = checkText.replace("O", "0")
    checkText = checkText.replace(".", "")
    checkText = checkText.replace(",", "")

    val price: Double? = checkText.toDoubleOrNull()

    return price
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun roundOffDecimal(number: Double): Double? {
    val df = DecimalFormat("###.###")
    df.roundingMode = RoundingMode.HALF_EVEN
    return df.format(number).toDouble()
}

fun sendTracker(eventName: String, activity: Context) {
    val bundle = Bundle()
    val currentTime = Calendar.getInstance().time
    bundle.putString("journey_id", InvoiceManager.journeyId)
    bundle.putString("click_time", currentTime.toString())
    bundle.putString("app_version", Constant.AppsVersion)

    FirebaseAnalytics.getInstance(activity).logEvent(eventName, bundle)
}

fun sendTrackerError(eventName: String, activity: Context, error: String) {
    val bundle = Bundle()
    val currentTime = Calendar.getInstance().time
    bundle.putString("journey_id", InvoiceManager.journeyId)
    bundle.putString("click_time", currentTime.toString())
    bundle.putString("app_version", Constant.AppsVersion)
    bundle.putString("error_client", error)

    FirebaseAnalytics.getInstance(activity).logEvent(eventName, bundle)
}
