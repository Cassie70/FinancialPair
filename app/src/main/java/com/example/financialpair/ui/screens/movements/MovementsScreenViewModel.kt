package com.example.financialpair.ui.screens.movements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialpair.data.entity.Movement
import com.example.financialpair.data.repository.MovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

class MovementsScreenViewModel(
    private val repository: MovementRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(MovementsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.movements.collect { movements ->
                _uiState.update {
                    it.copy(
                        movements = movements
                    )
                }
            }
        }
    }

    fun validateDescription(value: String): Boolean {
        return value.isNotBlank()
    }

    fun validateAmount(value: String): Boolean {
        return value.isNotBlank() && value.toDoubleOrNull() != null
    }

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(description = value, hasDescriptionError  = !validateDescription(value))
        }
    }

    fun onAmountChange(value: String) {
        val regex = Regex("^\\d*(\\.\\d{0,2})?$")

        if (value.isEmpty() || regex.matches(value)) {
            _uiState.update {
                it.copy(
                    amount = value,
                    hasAmountError = !validateAmount(value)
                )
            }
        }
    }

    fun insertMovement() {
        val descriptionValid = validateDescription(_uiState.value.description)
        val amountValid = validateAmount(_uiState.value.amount)

        _uiState.update {
            it.copy(
                hasDescriptionError = !descriptionValid,
                hasAmountError = !amountValid
            )
        }

        if (!descriptionValid || !amountValid) return

        val today = LocalDate.now()
        val date = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth

        viewModelScope.launch {
            repository.insert(
                Movement(
                    description = _uiState.value.description,
                    amount = (_uiState.value.amount.toDouble() * 100).roundToInt(),
                    date = date,
                    topicId = 0
                )
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            description = "",
                            amount = ""
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(error = e.message)
                    }
                }
        }
    }
}