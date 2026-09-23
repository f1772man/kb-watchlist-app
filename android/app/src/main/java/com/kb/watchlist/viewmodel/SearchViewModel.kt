package com.kb.watchlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kb.watchlist.model.StockSearchResult
import com.kb.watchlist.repository.WatchlistRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<StockSearchResult> = emptyList(),
    val addedStockCodes: Set<String> = emptySet(),
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: WatchlistRepository = WatchlistRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Load initial watchlist to know which stocks are already added
        loadCurrentWatchlist()

        // Debounced search flow (300ms)
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    executeSearch(query)
                }
        }
    }

    private fun loadCurrentWatchlist() {
        viewModelScope.launch {
            repository.getWatchlist().onSuccess { items ->
                val codes = items.map { it.code }.toSet()
                _uiState.update { it.copy(addedStockCodes = codes) }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchQueryFlow.value = newQuery
    }

    private suspend fun executeSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val result = repository.searchStocks(query)
        result.onSuccess { list ->
            _uiState.update { it.copy(results = list, isLoading = false) }
        }.onFailure { err ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "검색에 실패했습니다: ${err.localizedMessage}"
                )
            }
        }
    }

    fun toggleWatchlist(stock: StockSearchResult) {
        val isAlreadyAdded = _uiState.value.addedStockCodes.contains(stock.code)

        viewModelScope.launch {
            if (isAlreadyAdded) {
                // Remove from watchlist
                repository.removeFromWatchlist(stock.code).onSuccess {
                    _uiState.update {
                        it.copy(addedStockCodes = it.addedStockCodes - stock.code)
                    }
                }
            } else {
                // Add to watchlist
                repository.addToWatchlist(stock.code, stock.name).onSuccess {
                    _uiState.update {
                        it.copy(addedStockCodes = it.addedStockCodes + stock.code)
                    }
                }
            }
        }
    }
}
