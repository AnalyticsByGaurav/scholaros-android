package com.scholaros.erp.api

import com.scholaros.erp.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String = ""

    fun getService(session: SessionManager): ApiService {
        val baseUrl = session.baseUrl.ifEmpty { "http://localhost/school/" }

        // Rebuild Retrofit if the base URL changed (e.g. after server config change)
        if (retrofit == null || currentBaseUrl != baseUrl) {
            retrofit = buildRetrofit(baseUrl, session)
            currentBaseUrl = baseUrl
        }

        return retrofit!!.create(ApiService::class.java)
    }

    private fun buildRetrofit(baseUrl: String, session: SessionManager): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // set to NONE in production
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Call this after login / logout to force rebuild with new token
    fun reset() {
        retrofit = null
        currentBaseUrl = ""
    }
}
