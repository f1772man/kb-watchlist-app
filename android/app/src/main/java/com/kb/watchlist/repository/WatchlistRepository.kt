package com.kb.watchlist.repository

import com.kb.watchlist.model.*
import com.kb.watchlist.network.ApiClient

class WatchlistRepository {

    private val apiService get() = ApiClient.getService()

    suspend fun getWatchlist(): Result<List<WatchlistItem>> {
        return try {
            val response = apiService.getWatchlist()
            if (response.isSuccessful) {
                Result.success(response.body()?.watchlist ?: emptyList())
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToWatchlist(code: String, name: String?): Result<Unit> {
        return try {
            val response = apiService.addToWatchlist(AddWatchlistRequest(code = code, name = name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to add to watchlist: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromWatchlist(code: String): Result<Unit> {
        return try {
            val response = apiService.removeFromWatchlist(code)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove from watchlist: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuote(code: String): Result<StockQuote> {
        return try {
            val response = apiService.getQuote(code)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch quote for $code: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBatchQuotes(codes: List<String>): Result<List<StockQuote>> {
        if (codes.isEmpty()) return Result.success(emptyList())
        return try {
            val response = apiService.getBatchQuotes(BatchQuotesRequest(codes))
            if (response.isSuccessful) {
                Result.success(response.body()?.quotes ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch batch quotes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStocks(query: String): Result<List<StockSearchResult>> {
        return try {
            val response = apiService.searchStocks(query)
            if (response.isSuccessful) {
                Result.success(response.body()?.results ?: emptyList())
            } else {
                Result.failure(Exception("Search failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
