package com.app.appcenter.shared.model.storage

import com.app.appcenter.shared.model.datamodel.APKAvaialability
import com.app.appcenter.shared.model.datamodel.APKFileInfo

interface ApkDataStorageContract {
    fun saveApkMetaInfo(apkFileInfo: APKFileInfo)
    fun getFileAvailability(apkId: String): APKAvaialability
}