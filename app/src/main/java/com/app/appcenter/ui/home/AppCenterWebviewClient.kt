package com.app.appcenter.ui.home

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import com.app.appcenter.databinding.FragmentHomeBinding


internal class AppCenterWebViewClient(private val context: HomeFragment,
                                      private val _binding: FragmentHomeBinding,
                                      private val homeViewModel: HomeViewModel): WebViewClient() {

    override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest ): Boolean {
        super.shouldOverrideUrlLoading(webView, request)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (request.url.toString().contains(".apk") || (request.url.toString().contains(".aab")))
                startDownload(request.url.toString())
            }
        return false
    }

    private fun startDownload(downloadPath: String) {
        context.onDownloadStart(downloadPath)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        _binding.spinKit.visibility = View.VISIBLE
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        _binding.spinKit.visibility = View.GONE
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        url?.let{ homeViewModel.writeApkFolderNamesToPrefs(it) }
    }


}