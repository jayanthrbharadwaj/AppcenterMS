package com.app.appcenter.shared.model.repository

import com.app.appcenter.shared.model.datamodel.APKAvaialability
import com.app.appcenter.shared.model.datamodel.APKFileInfo

interface ApkSaveRepositoryContract {
    fun saveApkMetaInfo(apkFileInfo: APKFileInfo)
    fun getFileAvailability(apkId: String): APKAvaialability
}