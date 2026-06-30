package com.example.financialpair.ui.screens

import com.example.financialpair.data.entity.Movement

data class MovementsScreenState(
    val movements: List<Movement> = emptyList(),
    val description: String = "",
    val hasDescriptionError: Boolean = false,
    val hasAmountError: Boolean = false,
    val amount: String = "",
    val error: String? = null
)