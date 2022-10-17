package com.app.appcenter.ui.dashboard

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Environment
import androidx.lifecycle.*
import com.app.appcenter.ui.dashboard.adapter.FileMeta
import com.app.appcenter.ui.util.DOWNLOADS_STORE_KEY
import com.app.appcenter.ui.util.DOWNLOADS_STORE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DownloadsViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var appFileMetas: MutableLiveData<MutableList<FileMeta>>

    private lateinit var resolveInfo: MutableLiveData<List<ResolveInfo>>

    private suspend fun getListOfInstalledPackages(): List<ResolveInfo> {
        return withContext(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            val appName = loadFileMeta()[0].packageName
            val isExists =
                apps.filter { (it.activityInfo.packageName).equals(appName) }
            isExists
        }
    }

    fun getResolveInfo(): MutableLiveData<List<ResolveInfo>> {
        if(!::resolveInfo.isInitialized) {
            resolveInfo = MutableLiveData()
            viewModelScope.launch {
                resolveInfo.postValue(getListOfInstalledPackages())
            }
        }
        return resolveInfo
    }

    fun callLoadMeta(): MutableLiveData<MutableList<FileMeta>> {
        if(!::appFileMetas.isInitialized) {
            appFileMetas = MutableLiveData()
            viewModelScope.launch {
                appFileMetas.postValue(loadFileMeta())
            }
        }
        return appFileMetas
    }

    private suspend fun loadFileMeta(): MutableList<FileMeta> {
        return withContext(Dispatchers.IO) {
            val fileMetas = mutableListOf<FileMeta>()
            val fileList = getFileList()

            val pm = getApplication<Application>().packageManager

            fileList.forEach {
                val packageInfo = pm.getPackageArchiveInfo(it.absolutePath, 0)
                packageInfo?.let{pkgInfo->
                    val packageDisplay = pkgInfo.applicationInfo.packageName
                    fileMetas.add(
                        FileMeta(
                            packageInfo.versionName,
                            packageInfo.versionCode,
                            it.name,
                            getDateTime(it.lastModified()),
                            packageInfo.applicationInfo.loadIcon(pm),
                            packageInfo.packageName,
                            packageDisplay,
                            it.absolutePath
                        )
                    )
                }


            }
            fileMetas
        }

    }

    private fun getDateTime(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("MMM/dd/yyyy, HH:mm a", Locale.ENGLISH)
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun getFileList(): List<File> {
        val sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences(
            DOWNLOADS_STORE_NAME, Context.MODE_PRIVATE
        )

        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val subDir =
            File(directory.absolutePath.plus("/").plus(sharedPref.getString(DOWNLOADS_STORE_KEY, null)))
        val filesInSubdirectory = subDir.listFiles()
        return if(!filesInSubdirectory.isNullOrEmpty()) {
            Arrays.sort(filesInSubdirectory) { f1, f2 ->
                java.lang.Long.valueOf(f1.lastModified()).compareTo(f2.lastModified())
            }
            filesInSubdirectory.asList()
        } else emptyList()
    }
}