package com.technolovy.android.bayarberapa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_about_apps.*
import kotlinx.android.synthetic.main.activity_invoice_list_preview.*

class AboutAppsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_apps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        contacts.text = HtmlCompat.fromHtml(getString(R.string.about_app_contacts), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
