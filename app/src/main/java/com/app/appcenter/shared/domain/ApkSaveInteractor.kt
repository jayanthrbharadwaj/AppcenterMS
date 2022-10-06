package com.app.appcenter.shared.domain

import com.app.appcenter.shared.model.repository.ApkSaveRepositoryContract
import com.app.appcenter.shared.model.datamodel.APKAvaialability
import com.app.appcenter.shared.model.datamodel.APKFileInfo
class Empty
class ApkSaveInteractor: ApkSaveRepositoryContract {

    constructor() {

    }
    override fun saveApkMetaInfo(apkFileInfo: APKFileInfo) {
        TODO("Not yet implemented")
    }

    override fun getFileAvailability(apkId: String): APKAvaialability {
        TODO("Not yet implemented")
    }
}