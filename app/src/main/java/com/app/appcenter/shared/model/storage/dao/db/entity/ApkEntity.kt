package com.app.appcenter.shared.model.storage.dao.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.appcenter.shared.model.datamodel.APKAvaialability

@Entity(tableName = "apkEntity")
data class ApkEntity(
    @PrimaryKey (autoGenerate = true)
    var apkFilePath: String,
    var apkSize: String,
    var apkUrl: String,
    var apkVersion: String,
    var apkUrlLocation: String,
    var apkDownloadStatus: APKAvaialability
)