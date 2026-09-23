package com.kb.watchlist.model

import com.google.gson.annotations.SerializedName

data class StockQuote(
    @SerializedName("code") val code: String = "",
    @SerializedName("name") val name: String? = null,
    @SerializedName("currentPrice") val currentPrice: Long = 0L,
    @SerializedName("change") val change: Long = 0L,
    @SerializedName("changeSign") val changeSign: String? = "0", // "+", "-", "0"
    @SerializedName("changeRate") val changeRate: Double = 0.0,
    @SerializedName("volume") val volume: Long = 0L,
    @SerializedName("openPrice") val openPrice: Long = 0L,
    @SerializedName("highPrice") val highPrice: Long = 0L,
    @SerializedName("lowPrice") val lowPrice: Long = 0L,
    @SerializedName("updatedAt") val updatedAt: String = "",
    @SerializedName("error") val error: String? = null
) {
    val safeSign: String get() = changeSign ?: "0"
    val isRising: Boolean get() = safeSign == "+"
    val isFalling: Boolean get() = safeSign == "-"
    val isUnchanged: Boolean get() = safeSign == "0"
}

data class WatchlistItem(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("createdAt") val createdAt: String = ""
)

data class WatchlistResponse(
    @SerializedName("watchlist") val watchlist: List<WatchlistItem> = emptyList()
)

data class StockSearchResult(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("market") val market: String = "KOSPI"
)

data class SearchResponse(
    @SerializedName("results") val results: List<StockSearchResult> = emptyList()
)

data class BatchQuotesResponse(
    @SerializedName("quotes") val quotes: List<StockQuote> = emptyList()
)

data class AddWatchlistRequest(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String? = null
)

data class BatchQuotesRequest(
    @SerializedName("codes") val codes: List<String>
)
