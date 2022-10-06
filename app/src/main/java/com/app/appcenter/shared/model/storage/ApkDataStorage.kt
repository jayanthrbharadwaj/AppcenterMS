package com.app.appcenter.shared.model.storage

import com.app.appcenter.shared.model.datamodel.APKAvaialability
import com.app.appcenter.shared.model.datamodel.APKFileInfo

class ApkDataStorage: ApkDataStorageContract {
    override fun saveApkMetaInfo(apkFileInfo: APKFileInfo) {
        TODO("Not yet implemented")
    }

    override fun getFileAvailability(apkId: String): APKAvaialability {
        TODO("Not yet implemented")
    }
}