package com.example.financialpair.ui.screens.topics

import com.example.financialpair.data.entity.Category
import com.example.financialpair.data.entity.TopicWithCategory

data class TopicsScreenState(
    val topics: List<TopicWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val selectedCategory : Category? = null,
    val hasNameError: Boolean = false,
    val error: String? = null
)