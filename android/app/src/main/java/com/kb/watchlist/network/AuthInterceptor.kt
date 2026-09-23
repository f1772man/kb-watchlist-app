package com.kb.watchlist.network

import com.kb.watchlist.auth.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth header for health check if called
        if (originalRequest.url.encodedPath.endsWith("/health")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            AuthManager.getInstance().getIdToken()
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
