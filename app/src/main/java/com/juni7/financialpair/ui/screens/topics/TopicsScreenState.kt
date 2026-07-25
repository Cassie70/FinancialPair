package com.juni7.financialpair.ui.screens.topics

import com.juni7.financialpair.data.entity.Category
import com.juni7.financialpair.data.entity.TopicWithCategory

data class TopicsScreenState(
    val topics: List<TopicWithCategory> = emptyList(),
    val filteredTopics: List<TopicWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val selectedCategory : Category? = null,
    val editingTopic: TopicWithCategory? = null,
    val hasNameError: Boolean = false,
    val error: String? = null
)