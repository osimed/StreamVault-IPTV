package com.streamvault.app.ui.screens.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.data.preferences.PreferencesRepository
import com.streamvault.domain.model.PlaybackHistory
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.PlaybackHistoryRepository
import com.streamvault.domain.repository.ProviderRepository
import com.streamvault.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val seriesRepository: SeriesRepository,
    private val preferencesRepository: PreferencesRepository,
    private val playbackHistoryRepository: PlaybackHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState: StateFlow<SeriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            providerRepository.getActiveProvider()
                .filterNotNull()
                .collectLatest { provider ->
                    // Series by category
                    launch {
                        seriesRepository.getSeries(provider.id).collect { seriesList ->
                            val grouped = seriesList.groupBy { it.categoryName ?: "Uncategorized" }
                            _uiState.update { it.copy(seriesByCategory = grouped, isLoading = false) }
                        }
                    }
                    // Continue Watching — last 20 items for this provider
                    launch {
                        playbackHistoryRepository.getRecentlyWatchedByProvider(provider.id, limit = 20)
                            .collect { history ->
                                _uiState.update { it.copy(continueWatching = history) }
                            }
                    }
                }
        }

        viewModelScope.launch {
            preferencesRepository.parentalControlLevel.collect { level ->
                _uiState.update { it.copy(parentalControlLevel = level) }
            }
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        return preferencesRepository.parentalPin.first() == pin
    }
}

data class SeriesUiState(
    val seriesByCategory: Map<String, List<Series>> = emptyMap(),
    val continueWatching: List<PlaybackHistory> = emptyList(),
    val isLoading: Boolean = true,
    val parentalControlLevel: Int = 0
)
