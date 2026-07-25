package com.juni7.financialpair.ui.screens.movements

data class EditMovementState(
    val id: Long = 0,
    val description: String = "",
    val amount: String = "",
    val date: String = "", // YYYYMMDD as string for TextField, converted for DB
    val topicId: Int = 0,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val hasDescriptionError: Boolean = false,
    val hasAmountError: Boolean = false,
    val hasDateError: Boolean = false
)
