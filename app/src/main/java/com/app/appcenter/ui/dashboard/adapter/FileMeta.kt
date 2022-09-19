package com.app.appcenter.ui.dashboard.adapter

import android.graphics.drawable.Drawable

data class FileMeta (val version: String,
                     val versionCode: Int,
                     val name: String,
                     val date: String?,
                     val icon: Drawable,
                     val packageName: String,
                     val appName: String,
                     val absolutePath: String)