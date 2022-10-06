package com.app.appcenter.shared.model.storage.dao.db

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.appcenter.shared.model.storage.dao.db.entity.ApkEntity

@Dao
interface ApkFileDao {

    @Query("SELECT * FROM apkEntity ORDER BY apkId DESC")
    fun getAll(): List<ApkEntity>

    @Insert
    fun insertAll(vararg logs: ApkEntity)

    @Query("DELETE FROM apkEntity")
    fun deleteEntries()

    @Query("SELECT * FROM apkEntity ORDER BY apkId DESC")
    fun selectAllLogsCursor(): Cursor

    @Query("SELECT * FROM apkEntity WHERE apkId = :id")
    fun selectApkFileById(id: Long): Cursor?
}
