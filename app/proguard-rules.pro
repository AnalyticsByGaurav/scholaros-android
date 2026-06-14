# ── App classes ──────────────────────────────────────────────────────────────
# Keep ALL app classes: Activities, Fragments, ViewModels, Repositories, Session.
# Navigation Component loads fragments by full class name from nav_graph.xml —
# if R8 renames them the app crashes with ClassNotFoundException after login.
-keep class com.scholaros.erp.** { *; }

# ── Fragments (safety net even if the rule above covers them) ─────────────────
-keep public class * extends androidx.fragment.app.Fragment

# ── Retrofit + Gson ───────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Gson needs field names to match JSON keys
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ── EncryptedSharedPreferences + Tink crypto ──────────────────────────────────
# security-crypto 1.1.0-alpha06 sometimes has incomplete consumer rules
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# ── OkHttp ────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── Firebase / FCM ────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ── Navigation Component ──────────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ── ViewPager2 ────────────────────────────────────────────────────────────────
-keep class * extends androidx.viewpager2.adapter.FragmentStateAdapter

# ── ViewBinding ───────────────────────────────────────────────────────────────
-keep class com.scholaros.erp.databinding.** { *; }

# ── Lifecycle ─────────────────────────────────────────────────────────────────
-keep class * extends androidx.lifecycle.ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ── Suppress common library warnings ─────────────────────────────────────────
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
