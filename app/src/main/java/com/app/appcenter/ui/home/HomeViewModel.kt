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
import com.app.appcenter.ui.util.DOWNLOADS_STORE_JSON_KEY
import com.app.appcenter.ui.util.DOWNLOADS_STORE_KEY
import com.app.appcenter.ui.util.DOWNLOADS_STORE_NAME

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    fun startDownload(downloadPath: String, context: Context) {
        val uri = Uri.parse(downloadPath)
        DownloadManager.Request(uri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
            setTitle(getApkFolderName()) // Title for notification.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                getApkFolderName().plus("/").plus(uri.lastPathSegment)
            ) // Storage directory path
            val downloadId = (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?)?.enqueue(this) // This will start downloading

        }
    }

    fun writeApkFolderNamesToPrefs(url: String) {
        val uri = Uri.parse(url)
        if (uri.pathSegments.size == 5 && url.contains("tester_apps")) {
            val sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences(
                DOWNLOADS_STORE_NAME, Context.MODE_PRIVATE
            )
            sharedPref.edit().apply {
                //val updatedFolderNames = JSONArray().put(uri.lastPathSegment.toString()).toString()
                putString(DOWNLOADS_STORE_KEY, uri.lastPathSegment.toString())
                apply()
            }
        }
    }

    private fun getApkFolderName(): String? {
        val sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences(
            DOWNLOADS_STORE_NAME, Context.MODE_PRIVATE
        )
        return sharedPref.getString(DOWNLOADS_STORE_KEY, null)
    }

}