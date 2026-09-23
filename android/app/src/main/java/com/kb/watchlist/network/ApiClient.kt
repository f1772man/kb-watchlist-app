package com.kb.watchlist.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Default Cloud Run URL placeholder (or local Android emulator 10.0.2.2:8080)
    @Volatile
    var baseUrl: String = "https://kb-watchlist-backend-YOUR_PROJECT_NUMBER.asia-northeast3.run.app/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private var retrofit: Retrofit? = null
    private var cachedService: WatchlistApiService? = null

    fun getService(): WatchlistApiService {
        val currentRetrofit = retrofit
        if (currentRetrofit == null || currentRetrofit.baseUrl().toString() != baseUrl) {
            val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val newRetrofit = Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit = newRetrofit
            cachedService = newRetrofit.create(WatchlistApiService::class.java)
        }
        return cachedService!!
    }

    fun updateBaseUrl(newUrl: String) {
        baseUrl = newUrl
        retrofit = null
        cachedService = null
    }
}
