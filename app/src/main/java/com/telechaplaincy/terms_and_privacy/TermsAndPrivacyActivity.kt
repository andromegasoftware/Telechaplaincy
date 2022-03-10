package com.telechaplaincy.terms_and_privacy

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.R
import kotlinx.android.synthetic.main.activity_terms_and_privacy.*

class TermsAndPrivacyActivity : AppCompatActivity() {

    private var pageIndicator: String =
        ""   //this will show that from which activity open this activity. sign up activity or profile activity
    private var webPageLink: String = ""     //this will show which website webView will connect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_privacy)

        terms_and_privacy_progressBar.visibility = View.VISIBLE
        pageIndicator = intent.getStringExtra("activity_code").toString()

        if (pageIndicator == "terms_and_conditions") {
            webPageLink = "https://www.ihearyouapp.com/terms-of-service/"
        } else {
            webPageLink = "https://www.ihearyouapp.com/privacy-policy/"
        }

        terms_and_privacy_web_mail.webViewClient = WebViewClient()

        // this will load the url of the website
        terms_and_privacy_web_mail.loadUrl(webPageLink)

        // this will enable the javascript settings
        terms_and_privacy_web_mail.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        terms_and_privacy_web_mail.settings.setSupportZoom(true)
    }

    // Overriding WebViewClient functions
    inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            terms_and_privacy_progressBar.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}