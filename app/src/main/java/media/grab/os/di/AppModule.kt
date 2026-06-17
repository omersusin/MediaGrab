package media.grab.os.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig = AppConfig()
}

data class AppConfig(
    val apiBaseUrl: String = "https://api.mediagrab.app",
    val buildVersion: String = "1.0.0"
)
