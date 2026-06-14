# Retrofit + Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Data models — keep all fields for Gson deserialization
-keep class com.scholaros.erp.data.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Firebase / FCM
-keep class com.google.firebase.** { *; }

# Navigation Component
-keep class androidx.navigation.** { *; }
