package com.app.appcenter.shared.di

import android.content.Context
import androidx.room.Room
import com.app.appcenter.shared.model.storage.dao.ApkDao
import com.app.appcenter.shared.model.storage.dao.db.ApkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object ApkDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ApkDatabase {
        return Room.databaseBuilder(
            appContext,
            ApkDatabase::class.java,
            "logging.db"
        ).build()
    }

    @Provides
    fun provideApkDao(database: ApkDatabase): ApkDao {
        return database.apkDao()
    }
}
