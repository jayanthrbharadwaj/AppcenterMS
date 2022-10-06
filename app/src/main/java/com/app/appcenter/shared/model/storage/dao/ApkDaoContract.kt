package com.app.appcenter.shared.model.storage.dao

import com.app.appcenter.shared.model.datamodel.APKAvaialability
import com.app.appcenter.shared.model.datamodel.APKFileInfo

interface ApkDaoContract {
    fun saveApkMetaInfo(apkFileInfo: APKFileInfo)
    fun getFileAvailability(): APKAvaialability
}
