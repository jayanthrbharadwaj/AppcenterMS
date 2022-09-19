package com.app.appcenter.ui.dashboard.interfaces

import com.app.appcenter.ui.dashboard.adapter.FileMeta

interface DownloadFileClickListener {
    fun onShareClick(fileMeta: FileMeta, position: Int)
    fun onDeleteClick(fileMeta: FileMeta, position: Int)
    fun onInstallClick(fileMeta: FileMeta, position: Int)
}