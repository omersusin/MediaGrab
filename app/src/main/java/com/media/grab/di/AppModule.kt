package com.media.grab.di

import android.content.Context
import androidx.room.Room
import com.media.grab.data.local.AppDatabase
import com.media.grab.data.local.dao.DownloadDao
import com.media.grab.data.local.dao.GrabbedMediaDao
import com.media.grab.data.local.dao.HistoryDao
import com.media.grab.data.preferences.PreferencesManager
import com.media.grab.data.repository.DownloadRepository
import com.media.grab.data.repository.DownloadRepositoryImpl
import com.media.grab.data.repository.GrabberRepository
import com.media.grab.data.repository.GrabberRepositoryImpl
import com.media.grab.data.repository.HistoryRepository
import com.media.grab.data.repository.HistoryRepositoryImpl
import com.media.grab.grabber.CacheScanner
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class IoDispatcher
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class MainDispatcher

@Module @InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun db(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "media_grab.db").build()

    @Provides fun downloadDao(db: AppDatabase): DownloadDao = db.downloadDao()
    @Provides fun grabbedMediaDao(db: AppDatabase): GrabbedMediaDao = db.grabbedMediaDao()
    @Provides fun historyDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides @Singleton
    fun prefs(@ApplicationContext ctx: Context): PreferencesManager = PreferencesManager(ctx)

    @Provides @Singleton
    fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true).build()

    @Provides @IoDispatcher fun io(): CoroutineDispatcher = Dispatchers.IO
    @Provides @MainDispatcher fun main(): CoroutineDispatcher = Dispatchers.Main

    @Provides @Singleton
    fun cacheScanner(@ApplicationContext ctx: Context, @IoDispatcher io: CoroutineDispatcher): CacheScanner =
        CacheScanner(ctx, io)
}

@Module @InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository

    @Binds @Singleton
    abstract fun bindGrabberRepository(impl: GrabberRepositoryImpl): GrabberRepository

    @Binds @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository
}
