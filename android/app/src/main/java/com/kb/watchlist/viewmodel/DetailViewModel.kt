package com.kb.watchlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kb.watchlist.model.StockQuote
import com.kb.watchlist.repository.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val stockCode: String = "",
    val quote: StockQuote? = null,
    val isLoading: Boolean = true,
    val isRemoving: Boolean = false,
    val isRemoved: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val repository: WatchlistRepository = WatchlistRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadQuote(stockCode: String) {
        _uiState.update { it.copy(stockCode = stockCode, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.getQuote(stockCode)
            result.onSuccess { quote ->
                _uiState.update { it.copy(quote = quote, isLoading = false) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "시세를 가져오지 못했습니다: ${err.localizedMessage}"
                    )
                }
            }
        }
    }

    fun removeFromWatchlist(onSuccess: () -> Unit) {
        val code = _uiState.value.stockCode
        if (code.isEmpty()) return

        _uiState.update { it.copy(isRemoving = true) }
        viewModelScope.launch {
            val result = repository.removeFromWatchlist(code)
            result.onSuccess {
                _uiState.update { it.copy(isRemoving = false, isRemoved = true) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isRemoving = false,
                        errorMessage = "관심종목 삭제 실패: ${err.localizedMessage}"
                    )
                }
            }
        }
    }
}
