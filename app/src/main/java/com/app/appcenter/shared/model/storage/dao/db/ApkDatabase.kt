package com.app.appcenter.shared.model.storage.dao.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.appcenter.shared.model.storage.dao.ApkDao
import com.app.appcenter.shared.model.storage.dao.db.entity.ApkEntity
import java.security.SecureRandom

@Database(entities = [ApkEntity::class],version = 1, exportSchema = false)
abstract class ApkDatabase: RoomDatabase() {
    abstract fun apkDao(): ApkDao

    companion object {

        private val keyByteLength = 64
        private const val databaseName = "ApkData"

        @Volatile
        private var INSTANCE: ApkDatabase? = null

        fun getInstance(context: Context): ApkDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }

            }

        fun generateKey(size: Int): ByteArray {
            val key = ByteArray(size)
            SecureRandom().nextBytes(key)
            return key
        }

        private fun buildDatabase(context: Context): ApkDatabase {
//            val suppportFactory = SupportFactory(SecureStore(context).getDefaultKey())
            return Room.databaseBuilder(
                context.applicationContext,
                ApkDatabase::class.java,
                databaseName
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}