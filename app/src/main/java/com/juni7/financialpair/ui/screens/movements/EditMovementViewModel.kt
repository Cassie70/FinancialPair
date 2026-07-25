package com.juni7.financialpair.ui.screens.movements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.repository.MovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EditMovementViewModel(
    private val movementId: Long,
    private val repository: MovementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMovementState(id = movementId))
    val uiState = _uiState.asStateFlow()

    init {
        loadMovement()
    }

    private fun loadMovement() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val movement = repository.findById(movementId)
            if (movement != null) {
                _uiState.update {
                    it.copy(
                        description = movement.description,
                        amount = (movement.amount / 100.0).toString(),
                        date = movement.date.toString(),
                        topicId = movement.topicId,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Movement not found") }
            }
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, hasDescriptionError = value.isBlank()) }
    }

    fun onAmountChange(value: String) {
        val regex = Regex("^\\d*(\\.\\d{0,2})?$")
        if (value.isEmpty() || regex.matches(value)) {
            _uiState.update { it.copy(amount = value, hasAmountError = value.isBlank() || value.toDoubleOrNull() == null) }
        }
    }

    fun onDateChange(value: String) {
        // Simple validation for YYYYMMDD
        val isValid = value.length == 8 && value.toIntOrNull() != null
        _uiState.update { it.copy(date = value, hasDateError = !isValid) }
    }

    fun saveMovement() {
        val state = _uiState.value
        if (state.hasDescriptionError || state.hasAmountError || state.hasDateError ||
            state.description.isBlank() || state.amount.isBlank() || state.date.isBlank()
        ) {
            return
        }

        viewModelScope.launch {
            val movement = Movement(
                id = state.id,
                description = state.description,
                amount = (state.amount.toDouble() * 100).roundToInt(),
                date = state.date.toInt(),
                topicId = state.topicId
            )
            repository.update(movement)
                .onSuccess {
                    _uiState.update { it.copy(isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun resetNavigationFlags() {
        _uiState.update { it.copy(isSaved = false, isDeleted = false) }
    }

    fun deleteMovement() {
        viewModelScope.launch {
            val movement = repository.findById(movementId)
            if (movement != null) {
                repository.delete(movement)
                    .onSuccess {
                        _uiState.update { it.copy(isDeleted = true) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(error = e.message) }
                    }
            }
        }
    }
}
