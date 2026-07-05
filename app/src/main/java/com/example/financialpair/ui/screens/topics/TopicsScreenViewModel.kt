package com.example.financialpair.ui.screens.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialpair.data.entity.Category
import com.example.financialpair.data.entity.Topic
import com.example.financialpair.data.entity.TopicWithCategory
import com.example.financialpair.data.repository.CategoryRepository
import com.example.financialpair.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TopicsScreenViewModel(
    private val topicRepository: TopicRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TopicsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicRepository.topicsWithCategory.collect { topics ->
                _uiState.update {
                    it.copy(
                        topics = topics,
                        filteredTopics = filterTopics(topics, it.name, it.editingTopic != null)
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.categories.collect { categories ->
                _uiState.update {
                    it.copy(
                        categories = categories
                    )
                }
            }
        }
    }

    private fun filterTopics(
        topics: List<TopicWithCategory>,
        query: String,
        isEditing: Boolean
    ): List<TopicWithCategory> {
        if (isEditing || query.isBlank()) return topics
        return topics.filter { it.topic.name.contains(query, ignoreCase = true) }
    }

    fun onCategoryChange(category: Category) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
        val state = _uiState.value
        if (state.editingTopic != null) {
            updateTopic(state.editingTopic.topic.copy(categoryId = category.id))
        }
    }

    fun validateName(value: String): Boolean {
        return value.isNotBlank()
    }

    fun onNameChange(value: String) {
        _uiState.update {
            val isEditing = it.editingTopic != null
            it.copy(
                name = value,
                hasNameError = !validateName(value),
                filteredTopics = filterTopics(it.topics, value, isEditing)
            )
        }
        val state = _uiState.value
        if (state.editingTopic != null && validateName(value)) {
            updateTopic(state.editingTopic.topic.copy(name = value))
        }
    }

    private fun updateTopic(topic: Topic) {
        viewModelScope.launch {
            topicRepository.update(topic).onFailure { e ->
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    fun onTopicClick(topic: TopicWithCategory) {
        if (_uiState.value.editingTopic?.topic?.id == topic.topic.id) {
            clearSelection()
            return
        }
        val category = _uiState.value.categories.find { it.name == topic.categoryName }
        _uiState.update {
            it.copy(
                editingTopic = topic,
                name = topic.topic.name,
                selectedCategory = category,
                hasNameError = false,
                filteredTopics = it.topics // Show all topics when editing
            )
        }
    }

    fun clearSelection() {
        _uiState.update {
            it.copy(
                editingTopic = null,
                name = "",
                selectedCategory = null,
                hasNameError = false,
                error = null,
                filteredTopics = it.topics
            )
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            topicRepository.delete(topic).onSuccess {
                if (_uiState.value.editingTopic?.topic?.id == topic.id) {
                    clearSelection()
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    fun insertTopic() {
        val state = _uiState.value
        val nameValid = validateName(state.name)
        if (state.selectedCategory == null || !nameValid) {
            _uiState.update { it.copy(hasNameError = !nameValid) }
            return
        }

        viewModelScope.launch {
            topicRepository.insert(
                Topic(
                    name = state.name,
                    categoryId = state.selectedCategory.id
                )
            ).onSuccess {
                clearSelection()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }
}
