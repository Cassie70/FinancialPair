package com.juni7.financialpair.ui.screens.movements

import androidx.compose.ui.text.input.TextFieldValue
import com.juni7.financialpair.data.model.TopicWithLastAmount

data class MovementsScreenState(
    val logoUrls: Map<String, String> = emptyMap(),
    val totalsByDate: Map<Int, Int> = emptyMap(),
    val description: TextFieldValue = TextFieldValue(""),
    val suggestedTopics: List<TopicWithLastAmount> = emptyList(),
    val hasDescriptionError: Boolean = false,
    val hasAmountError: Boolean = false,
    val amount: String = "",
    val error: String? = null
)
