package com.scholaros.erp.data.repository

import com.scholaros.erp.api.ApiClient
import com.scholaros.erp.api.ApiService
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.LoginRequest
import com.scholaros.erp.utils.Resource

class AuthRepository(private val session: SessionManager) {

    private fun service(): ApiService = ApiClient.getService(session)

    suspend fun login(baseUrl: String, email: String, password: String): Resource<String> {
        return try {
            // Temporarily set base URL for this call
            session.baseUrl = baseUrl
            ApiClient.reset()

            val response = service().login(LoginRequest(email, password))
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data!!
                session.saveSession(baseUrl, data.token, data.user)
                Resource.Success("Login successful")
            } else {
                session.baseUrl = ""
                Resource.Error(body?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            session.baseUrl = ""
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun logout(): Resource<Unit> {
        return try {
            service().logout()
            session.clearSession()
            ApiClient.reset()
            Resource.Success(Unit)
        } catch (e: Exception) {
            session.clearSession()
            ApiClient.reset()
            Resource.Success(Unit)   // always succeed locally
        }
    }
}
