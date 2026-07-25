package com.juni7.financialpair.ui.screens.movements

import com.juni7.financialpair.data.entity.MovementWithTopic

data class MovementsScreenState(
    val movements: List<MovementWithTopic> = emptyList(),
    val logoUrls: Map<String, String> = emptyMap(),
    val description: String = "",
    val hasDescriptionError: Boolean = false,
    val hasAmountError: Boolean = false,
    val amount: String = "",
    val error: String? = null
)
