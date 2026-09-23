package com.kb.watchlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kb.watchlist.auth.AuthManager
import com.kb.watchlist.model.StockQuote
import com.kb.watchlist.model.WatchlistItem
import com.kb.watchlist.repository.WatchlistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val items: List<WatchlistItem> = emptyList(),
    val quotes: Map<String, StockQuote> = emptyMap(),
    val errorMessage: String? = null,
    val lastUpdated: Long = 0L
)

class HomeViewModel(
    private val repository: WatchlistRepository = WatchlistRepository(),
    private val authManager: AuthManager = AuthManager.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private val POLLING_INTERVAL_MS = 25_000L // 25 seconds polling

    init {
        initializeAndLoad()
    }

    fun initializeAndLoad() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authManager.ensureAuthenticated()
                loadWatchlistAndQuotes()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "인증 또는 데이터를 불러오지 못했습니다: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            loadWatchlistAndQuotes()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun loadWatchlistAndQuotes() {
        val watchlistResult = repository.getWatchlist()
        watchlistResult.onSuccess { items ->
            _uiState.update { it.copy(items = items, isLoading = false) }
            if (items.isNotEmpty()) {
                fetchQuotes(items.map { it.code })
            } else {
                _uiState.update { it.copy(quotes = emptyMap()) }
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "관심종목을 가져오지 못했습니다: ${error.localizedMessage}"
                )
            }
        }
    }

    private suspend fun fetchQuotes(codes: List<String>) {
        if (codes.isEmpty()) return
        val quotesResult = repository.getBatchQuotes(codes)
        quotesResult.onSuccess { quotesList ->
            val quotesMap = quotesList.associateBy { it.code }
            _uiState.update {
                it.copy(
                    quotes = it.quotes + quotesMap,
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }.onFailure {
            // Silently keep existing cached quotes or log
        }
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(POLLING_INTERVAL_MS)
                val currentCodes = _uiState.value.items.map { it.code }
                if (currentCodes.isNotEmpty()) {
                    fetchQuotes(currentCodes)
                }
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
