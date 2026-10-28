package au.edu.jcu.assessment.utilityapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AirQualityUiState(
    val selectedCity: String = "Singapore",
    val showDetails: Boolean = true,
    val alertThreshold: Int = 100,
    val isLoading: Boolean = false,
    val response: AirQualityResponse? = null,
    val errorMessage: String? = null
)

class AirQualityViewModel(
    private val repository: AirQualityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AirQualityUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        refresh()
    }

    fun selectCity(city: String) {
        if (city == _uiState.value.selectedCity) return

        _uiState.update { it.copy(selectedCity = city) }
        refresh()
    }

    fun setShowDetails(show: Boolean) {
        _uiState.update { it.copy(showDetails = show) }
    }

    fun setAlertThreshold(threshold: Int) {
        _uiState.update { it.copy(alertThreshold = threshold) }
    }

    fun refresh() {
        // Cancel the previous request when changing cities or refreshing.
        fetchJob?.cancel()

        val city = _uiState.value.selectedCity

        _uiState.update {
            it.copy(
                isLoading = true,
                response = null,
                errorMessage = null
            )
        }

        fetchJob = viewModelScope.launch {
            try {
                val response = repository.getAirQuality(city)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        response = response
                    )
                }
            } catch (exception: CancellationException) {
                // Cancellation is not a network failure.
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Unable to load air quality. Please try again."
                    )
                }
            }
        }
    }

    class Factory(
        private val repository: AirQualityRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AirQualityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AirQualityViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}