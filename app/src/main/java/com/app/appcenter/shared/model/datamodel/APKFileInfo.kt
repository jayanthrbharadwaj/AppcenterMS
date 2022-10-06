package com.app.appcenter.shared.model.datamodel

data class APKFileInfo (
    val apkId: String,
    val apkPath: String,
    val apkSize: String,
    val apkVersion: String,
    val apkUrlLocation: String,
    val apkDownloadStatus: APKAvaialability,
)
