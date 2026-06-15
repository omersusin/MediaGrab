# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class media.grab.os.**$$serializer { *; }
-keepclassmembers class media.grab.os.** { *** Companion; }
-keepclasseswithmembers class media.grab.os.** { kotlinx.serialization.KSerializer serializer(...); }
# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
# Shizuku
-keep class rikka.shizuku.** { *; }
# YouTubeDL
-keep class com.yausername.youtubedl_android.** { *; }
# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
