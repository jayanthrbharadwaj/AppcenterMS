package com.app.appcenter.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.appcenter.R
import com.app.appcenter.ui.dashboard.interfaces.DownloadFileClickListener

class FileViewerAdapter(val downloadFileClickListener: DownloadFileClickListener,
                        val fileMetas: MutableList<FileMeta>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_alert_view_holder, parent, false)
        return FileViewHolder(itemView, downloadFileClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val fileViewHolder = holder as FileViewHolder
        fileViewHolder.bindViewHolder(fileMetas[position], position)
    }

    override fun getItemCount(): Int {
        return fileMetas.size
    }

    class FileViewHolder: RecyclerView.ViewHolder {
        lateinit var version: TextView
        var fileName: TextView
        var appName: TextView
        var dateName: TextView
        var versionCode: TextView
        var shareBtn: ImageView
        var deleteBtn: ImageView
        var installApk: ImageView
        var downloadFileClickListener: DownloadFileClickListener

        constructor(view:View, downloadFileClickListener: DownloadFileClickListener) : super(view) {
            version = view.findViewById(R.id.fileId)
            this.downloadFileClickListener = downloadFileClickListener
            fileName = view.findViewById(R.id.fileName)
            dateName = view.findViewById(R.id.dateName)
            appName = view.findViewById(R.id.appName)
            versionCode = view.findViewById(R.id.versionCode)
            shareBtn = view.findViewById(R.id.shareBtn)
            deleteBtn = view.findViewById(R.id.deleteBtn)
            installApk = view.findViewById(R.id.installApk)
        }

        fun bindViewHolder(fileMeta: FileMeta, position: Int) {
            dateName.text = "Downloaded on: "+fileMeta.date.toString()
            fileName.text = fileMeta.name
            version.text = fileMeta.version
            appName.text = fileMeta.appName
            versionCode.text = "Version Code: ".plus(fileMeta.versionCode)
            deleteBtn.setOnClickListener{
                downloadFileClickListener.onDeleteClick(fileMeta, position)
            }
            shareBtn.setOnClickListener{
                downloadFileClickListener.onShareClick(fileMeta, position)
            }
            installApk.setOnClickListener{
                downloadFileClickListener.onInstallClick(fileMeta, position)
            }

        }
    }
}
