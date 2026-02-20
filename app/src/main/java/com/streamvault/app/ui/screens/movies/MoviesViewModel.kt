package com.streamvault.app.ui.screens.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.data.preferences.PreferencesRepository
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.PlaybackHistory
import com.streamvault.domain.repository.MovieRepository
import com.streamvault.domain.repository.PlaybackHistoryRepository
import com.streamvault.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val movieRepository: MovieRepository,
    private val preferencesRepository: PreferencesRepository,
    private val playbackHistoryRepository: PlaybackHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            providerRepository.getActiveProvider()
                .filterNotNull()
                .collectLatest { provider ->
                    // Movies by category
                    launch {
                        movieRepository.getMovies(provider.id).collect { movies ->
                            val grouped = movies.groupBy { it.categoryName ?: "Uncategorized" }
                            _uiState.update { it.copy(moviesByCategory = grouped, isLoading = false) }
                        }
                    }
                    // Continue Watching — last 20 movie/series items for this provider
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

data class MoviesUiState(
    val moviesByCategory: Map<String, List<Movie>> = emptyMap(),
    val continueWatching: List<PlaybackHistory> = emptyList(),
    val isLoading: Boolean = true,
    val parentalControlLevel: Int = 0
)
