package com.bytebuddy.kharchaaa

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.webkit.DownloadListener
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileOutputStream

/**
 * Kharchaaa v3.0 — thin WebView shell around the web app in assets/index.html.
 * All expense-tracking logic lives in the web app; this Activity only hosts it
 * and makes its file downloads (PDF / JSON / CSV) actually save on Android.
 */
class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    // <input type="file"> (Data Import) needs a file chooser on Android 5+.
    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileChooserCallback?.onReceiveValue(uri?.let { arrayOf(it) })
        fileChooserCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            // The page is loaded from file:// and loads jsPDF from an https CDN.
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        webView.addJavascriptInterface(AndroidBridge(this), "AndroidBridge")

        webView.webViewClient = object : WebViewClient() {
            // Keep the app itself in the WebView; hand everything else to the system.
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.startsWith("file:///android_asset/") || url == "about:blank") return false
                return try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    true
                } catch (e: Exception) {
                    true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback
                filePicker.launch("*/*")
                return true
            }
        }

        // Any download the WebView itself triggers (http/https links).
        // blob: downloads from the app are intercepted by AndroidBridge instead,
        // because DownloadManager cannot fetch blob: URLs.
        webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            if (url.startsWith("blob:")) {
                Toast.makeText(this, "Download app ke andar save ho raha hai.", Toast.LENGTH_SHORT).show()
                return@DownloadListener
            }
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                    .setMimeType(mimeType)
                    .addRequestHeader("User-Agent", userAgent)
                    .setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "Kharchaaa/" + URLUtil.guessFileName(url, contentDisposition, mimeType)
                    )
                (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
                Toast.makeText(this, "Download shuru ho gaya.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Download nahi ho saka.", Toast.LENGTH_LONG).show()
            }
        })

        // Back button: go back in WebView history first, exit the app only at the root.
        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) webView.goBack() else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}

/**
 * Bridge called from the web app's download helper (assets/index.html only).
 * The web app checks `window.AndroidBridge` before using it, so the same page
 * still works as a plain website when the bridge is absent.
 */
class AndroidBridge(private val context: Context) {

    @JavascriptInterface
    fun downloadText(fileName: String, content: String, mimeType: String) {
        saveBytes(fileName, content.toByteArray(Charsets.UTF_8), mimeType)
    }

    @JavascriptInterface
    fun downloadBase64(fileName: String, base64: String, mimeType: String) {
        try {
            saveBytes(fileName, Base64.decode(base64, Base64.DEFAULT), mimeType)
        } catch (e: IllegalArgumentException) {
            showToast("Download nahi ho saka.")
        }
    }

    private fun saveBytes(fileName: String, bytes: ByteArray, mimeType: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: public Downloads/Kharchaaa via MediaStore, no permission needed.
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Kharchaaa")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("MediaStore insert failed")
                resolver.openOutputStream(uri)?.use { it.write(bytes) }
                    ?: throw IllegalStateException("Could not open output stream")
            } else {
                // Android 9 and below: app-private external dir, no permission needed.
                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Kharchaaa")
                if (!dir.exists()) dir.mkdirs()
                FileOutputStream(File(dir, fileName)).use { it.write(bytes) }
            }
            showToast("$fileName Downloads / Kharchaaa me save ho gaya!")
        } catch (e: Exception) {
            showToast("Download nahi ho saka.")
        }
    }

    private fun showToast(msg: String) {
        // JavascriptInterface methods run off the main thread.
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
