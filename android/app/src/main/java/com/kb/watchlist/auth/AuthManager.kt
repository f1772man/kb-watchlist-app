package com.kb.watchlist.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthManager private constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun ensureAuthenticated(): FirebaseUser {
        val current = auth.currentUser
        if (current != null) {
            return current
        }

        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: throw IllegalStateException("Anonymous sign in returned null user")
            Log.d(TAG, "Signed in anonymously as UID: ${user.uid}")
            user
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign in failed", e)
            throw e
        }
    }

    suspend fun getIdToken(forceRefresh: Boolean = false): String? {
        val user = auth.currentUser ?: ensureAuthenticated()
        return try {
            val tokenResult = user.getIdToken(forceRefresh).await()
            tokenResult.token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve Firebase ID token", e)
            null
        }
    }

    companion object {
        private const val TAG = "AuthManager"

        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(): AuthManager =
            instance ?: synchronized(this) {
                instance ?: AuthManager().also { instance = it }
            }
    }
}
