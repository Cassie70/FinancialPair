package com.juni7.financialpair.ui.screens.topics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.juni7.financialpair.data.entity.Category
import com.juni7.financialpair.data.entity.Topic
import com.juni7.financialpair.data.entity.TopicWithCategory
import com.juni7.financialpair.data.repository.CategoryRepository
import com.juni7.financialpair.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TopicsScreenViewModel(
    private val topicRepository: TopicRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TopicsScreenState())
    val uiState = _uiState.asStateFlow()

    private val inProgressFetches = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            topicRepository.topicsWithCategory.collect { topics ->
                _uiState.update {
                    it.copy(
                        topics = topics,
                        filteredTopics = filterTopics(topics, it.name, it.editingTopic != null)
                    )
                }
                fetchLogosForTopics(topics)
            }
        }
        viewModelScope.launch {
            categoryRepository.categories.collect { categories ->
                _uiState.update {
                    it.copy(
                        categories = categories
                    )
                }
                fetchLogosForCategories(categories)
            }
        }
    }

    private fun fetchLogosForTopics(topics: List<TopicWithCategory>) {
        topics.forEach { item ->
            fetchLogo(item.topic.name, item.category.name, item.topic.logoUrl) { url ->
                topicRepository.updateLogoUrl(item.topic.id, url)
            }
        }
    }

    private fun fetchLogosForCategories(categories: List<Category>) {
        categories.forEach { category ->
            fetchLogo(category.name, null, null) { /* No persist category logos yet */ }
        }
    }

    private fun fetchLogo(name: String, fallbackName: String?, cachedUrl: String?, onUrlFetched: suspend (String) -> Unit = {}) {
        val currentUrls = _uiState.value.logoUrls
        
        // Use cached URL if available and not already in state
        if (!currentUrls.containsKey(name) && cachedUrl != null) {
            _uiState.update {
                it.copy(logoUrls = it.logoUrls + (name to cachedUrl))
            }
        }

        if (!currentUrls.containsKey(name) && !inProgressFetches.contains(name)) {
            inProgressFetches.add(name)
            viewModelScope.launch {
                try {
                    val path = "logos/${name.trim().lowercase().replace(' ', '-')}.svg"
                    val url = try {
                        Firebase.storage.reference.child(path).downloadUrl.await().toString()
                    } catch (e: Exception) {
                        if (fallbackName != null) {
                            val fallbackPath = "logos/${fallbackName.trim().lowercase().replace(' ', '-')}.svg"
                            Firebase.storage.reference.child(fallbackPath).downloadUrl.await().toString()
                        } else {
                            throw e
                        }
                    }

                    _uiState.update {
                        it.copy(logoUrls = it.logoUrls + (name to url))
                    }
                    onUrlFetched(url)
                } catch (e: Exception) {
                    Log.e("TopicsVM", "Error fetching logo for $name: ${e.message}")
                } finally {
                    inProgressFetches.remove(name)
                }
            }
        }
    }

    private fun filterTopics(
        topics: List<TopicWithCategory>,
        query: String,
        isEditing: Boolean
    ): List<TopicWithCategory> {
        val filtered = if (isEditing || query.isBlank()) {
            topics
        } else {
            topics.filter { it.topic.name.contains(query, ignoreCase = true) }
        }
        return filtered.sortedWith(
            compareBy<TopicWithCategory> { it.category.name }
                .thenBy { it.topic.name }
        )
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
        _uiState.update {
            it.copy(
                editingTopic = topic,
                name = topic.topic.name,
                selectedCategory = topic.category,
                hasNameError = false,
                filteredTopics = filterTopics(it.topics, "", true)
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
                filteredTopics = filterTopics(it.topics, "", false)
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
