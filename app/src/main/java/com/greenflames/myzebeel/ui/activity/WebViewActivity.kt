package com.greenflames.myzebeel.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.greenflames.myzebeel.R
import com.greenflames.myzebeel.helpers.Global.AboutUsUrl
import com.greenflames.myzebeel.helpers.Global.KNetUrl
import com.greenflames.myzebeel.helpers.LoadingDialog
import com.greenflames.myzebeel.network.Apis.BASE_URL

class WebViewActivity : AppCompatActivity() {

    private lateinit var webview: WebView
    private lateinit var backImg: ImageButton
    private var secondTime = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        LoadingDialog.showLoadingDialog(this@WebViewActivity, "Loading...")

        backImg = findViewById(R.id.web_back_img)
        webview = findViewById(R.id.knet_webView)
        val settings = webview.settings
        settings?.javaScriptEnabled = true
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                LoadingDialog.cancelLoading()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                //val htmlData ="<html><body><div align=\"center\" >Sorry!<br>Unable to connect to the page you have requested..<br></div></body>"
                //view?.loadUrl("about:blank")
                //view?.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8",null)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                if (url != null) {
                    Log.e("url", url)
                    val successBool = url.contains(BASE_URL + "checkout/cart/", ignoreCase = true) || url.contains(BASE_URL + "checkout/onepage/mobilesuccess", ignoreCase = true)
                    val failBool = url.contains(BASE_URL + "knet/payment/fail", ignoreCase = true)
                    println("$successBool $failBool")
                    if (successBool && secondTime) {
                        //println("successBool")
                        val intent = Intent(this@WebViewActivity, OrderPlacedActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (failBool && secondTime) {
                        //println("failBool")
                        val intent = Intent(this@WebViewActivity, OrderCancelledActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    secondTime = true
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }

        val url = KNetUrl

        //val url = "https://www.kpaytest.com.kw/kpg/PaymentHTTP.htm?param=paymentInit&trandata=e0b2085b67f44cb22a5eb95d2ed3d565545226b0b15a7b9ae56218850865f93d6d365ebbc8f17efe7f1fa6fe33747091359ec1c0b83cf1d9edfe7ac68be79d993f1c1cefa8485f33b3c66d29653d881e66504c18b782bc403029c721859c835c9fa0c72e384c0711dac4b7702c504d228649994d7623f900bba5704a33d3796691897e252088be59b21ba1843c238770e25e2fbe9f91e4c7ce6e78dba13b04187fdbc7f0570281e450589f8d670872189907cfdbcda404f17da24ea6ee4fbb9374533f475c2345b6315c716520824df0&tranportalId=247001&responseURL=http://demo.myzebeel.com/knet/payment/success&errorURL=errorURL=http://demo.myzebeel.com/knet/payment/fail"

        webview.loadUrl(url)

        backImg.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (KNetUrl != AboutUsUrl) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webview.onPause()
    }
}