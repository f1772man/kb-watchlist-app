package com.kb.watchlist.network

import com.kb.watchlist.model.*
import retrofit2.Response
import retrofit2.http.*

interface WatchlistApiService {

    @GET("quote/{code}")
    suspend fun getQuote(
        @Path("code") code: String
    ): Response<StockQuote>

    @POST("quotes")
    suspend fun getBatchQuotes(
        @Body request: BatchQuotesRequest
    ): Response<BatchQuotesResponse>

    @GET("watchlist")
    suspend fun getWatchlist(): Response<WatchlistResponse>

    @POST("watchlist")
    suspend fun addToWatchlist(
        @Body request: AddWatchlistRequest
    ): Response<Map<String, Any>>

    @DELETE("watchlist/{code}")
    suspend fun removeFromWatchlist(
        @Path("code") code: String
    ): Response<Map<String, Any>>

    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String
    ): Response<SearchResponse>
}
