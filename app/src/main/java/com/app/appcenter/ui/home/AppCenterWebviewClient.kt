package com.app.appcenter.ui.dashboard

import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.View
import android.webkit.*
import com.app.appcenter.R
import com.app.appcenter.databinding.FragmentHomeBinding


internal class AppCenterWebViewClient(private val context: Context,
                                      private val _binding: FragmentHomeBinding): WebViewClient() {

    val jsonKey = "jaiHanumanta"
    var sharedPref: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.shared_prefs_store), Context.MODE_PRIVATE)

    override fun shouldOverrideUrlLoading(webView: WebView, request: String ): Boolean {
        super.shouldOverrideUrlLoading(webView, request)
        if(request.contains(".apk")) {
            startDownload(request)
            return true
        }
        return false
    }

    private fun startDownload(downloadPath: String) {
        val uri = Uri.parse(downloadPath)
        DownloadManager.Request(uri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
            setTitle(uri.lastPathSegment) // Title for notification.
            setVisibleInDownloadsUi(true)
            val index = uri.lastPathSegment.indexOfFirst { c -> c =='.' }
            val path = uri.lastPathSegment.substring(0,index)
            sharedPref.getString(jsonKey, null).let{
                if(it.isNullOrEmpty())
                    sharedPref.edit().apply {
                        putString(jsonKey, path)
                        commit()
                }
            }
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path.plus("/").plus(uri.lastPathSegment)
            ) // Storage directory path
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?)?.enqueue(this) // This will start downloading
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        _binding.spinKit.visibility = View.VISIBLE
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        _binding.spinKit.visibility = View.GONE
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

}