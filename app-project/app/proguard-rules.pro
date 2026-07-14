# ─── Room (KSP) ────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.paging.**

# ─── Kotlin Serialization ─────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep,includedescriptorclasses class com.stufflocate.app.**$$serializer { *; }
-keepclassmembers class com.stufflocate.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.stufflocate.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.stufflocate.app.domain.model.** { *; }

# ─── Kotlin Coroutines ────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ─── Compose / UI ─────────────────────────────────────────────
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ─── Activity / Application ───────────────────────────────────
-keep class com.stufflocate.app.MainActivity { *; }
-keep class com.stufflocate.app.StuffLocateApplication { *; }

# ─── Parcelable / Serialization support ───────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ─── Kotlin metadata (required by Room + Serialization) ──────
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# ─── Room query-result POJOs (non-entity data classes) ────────
-keep class com.stufflocate.app.data.local.entity.** { *; }
