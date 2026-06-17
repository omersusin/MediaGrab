package media.grab.os.di

data class AppConfig(
    val apiBaseUrl: String = "https://api.mediagrab.app",
    val buildVersion: String = "1.0.0"
)

object AppModule {
    fun provideAppConfig(): AppConfig = AppConfig()
}
