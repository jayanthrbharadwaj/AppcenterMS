package com.app.appcenter.ui.home

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.appcenter.R

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val jsonKey = "jaiHanumanta"

    val text: LiveData<String> = _text

    fun startDownload(downloadPath: String, sharedPrefs: SharedPreferences?, context: Context) {
        val uri = Uri.parse(downloadPath)
        DownloadManager.Request(uri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
            setTitle(uri.lastPathSegment) // Title for notification.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            val index = uri.lastPathSegment?.indexOfFirst { c -> c =='.' }
            val path = uri.lastPathSegment?.substring(0, index!!)
            sharedPrefs?.getString(jsonKey, null)?.let{
                sharedPrefs.edit().apply {
                    putString(jsonKey, path)
                    apply()
                }
            }
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path.plus("/").plus(uri.lastPathSegment)
            ) // Storage directory path
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?)?.enqueue(this) // This will start downloading
        }
    }
}