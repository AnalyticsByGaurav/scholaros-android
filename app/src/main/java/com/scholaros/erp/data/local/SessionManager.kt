package com.scholaros.erp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.scholaros.erp.data.model.User

class SessionManager(context: Context) {

    private val prefs: SharedPreferences

    init {
        val appContext = context.applicationContext
        prefs = try {
            val masterKey = MasterKey.Builder(appContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                appContext,
                "scholaros_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            // Fallback for devices where the Android Keystore/Tink fails
            appContext.getSharedPreferences("scholaros_prefs_plain", Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val KEY_TOKEN      = "auth_token"
        private const val KEY_BASE_URL   = "base_url"
        private const val KEY_USER_ID    = "user_id"
        private const val KEY_USER_NAME  = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE  = "user_role"
        private const val KEY_ROLE_NAME  = "role_name"
        private const val KEY_SCHOOL_ID  = "school_id"
        private const val KEY_BRANCH_ID  = "branch_id"
        private const val KEY_PHOTO_URL  = "photo_url"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Base URL of the ScholarOS server (e.g. "http://yourschool.com/school/")
    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, "") ?: ""
        set(value) { prefs.edit().putString(KEY_BASE_URL, value.trimEnd('/') + '/').apply() }

    var token: String
        get() = prefs.getString(KEY_TOKEN, "") ?: ""
        set(value) { prefs.edit().putString(KEY_TOKEN, value).apply() }

    var userId: Int
        get() = prefs.getInt(KEY_USER_ID, 0)
        set(value) { prefs.edit().putInt(KEY_USER_ID, value).apply() }

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) { prefs.edit().putString(KEY_USER_NAME, value).apply() }

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) { prefs.edit().putString(KEY_USER_EMAIL, value).apply() }

    var userRole: String
        get() = prefs.getString(KEY_USER_ROLE, "") ?: ""
        set(value) { prefs.edit().putString(KEY_USER_ROLE, value).apply() }

    var roleName: String
        get() = prefs.getString(KEY_ROLE_NAME, "") ?: ""
        set(value) { prefs.edit().putString(KEY_ROLE_NAME, value).apply() }

    var schoolId: Int
        get() = prefs.getInt(KEY_SCHOOL_ID, 0)
        set(value) { prefs.edit().putInt(KEY_SCHOOL_ID, value).apply() }

    var branchId: Int
        get() = prefs.getInt(KEY_BRANCH_ID, 0)
        set(value) { prefs.edit().putInt(KEY_BRANCH_ID, value).apply() }

    var photoUrl: String
        get() = prefs.getString(KEY_PHOTO_URL, "") ?: ""
        set(value) { prefs.edit().putString(KEY_PHOTO_URL, value).apply() }

    val isLoggedIn: Boolean get() = token.isNotEmpty() && baseUrl.isNotEmpty()

    fun saveSession(baseUrl: String, token: String, user: User) {
        this.baseUrl    = baseUrl
        this.token      = token
        this.userId     = user.id
        this.userName   = user.name
        this.userEmail  = user.email
        this.userRole   = user.role
        this.roleName   = user.roleName
        this.schoolId   = user.schoolId
        this.branchId   = user.branchId
        this.photoUrl   = user.photoUrl ?: ""
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // Role helpers
    val isAdmin   get() = userRole in listOf("super_admin","school_admin","branch_admin","principal")
    val isTeacher get() = userRole == "teacher"
    val isStudent get() = userRole == "student"
    val isParent  get() = userRole == "parent"
}
