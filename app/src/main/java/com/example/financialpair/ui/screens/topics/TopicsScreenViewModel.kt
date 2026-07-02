package com.example.financialpair.ui.screens.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialpair.data.entity.Category
import com.example.financialpair.data.entity.Topic
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
                        topics = topics
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

    fun onCategoryChange(category: Category) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    fun validateName(value: String): Boolean {
        return value.isNotBlank()
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value, hasNameError = !validateName(value))
        }
    }

    fun insertTopic(){
        val nameValid = validateName(_uiState.value.name)
        if (_uiState.value.selectedCategory == null || !nameValid) return

        viewModelScope.launch {
            topicRepository.insert(
                Topic(
                    name = _uiState.value.name,
                    categoryId = _uiState.value.selectedCategory!!.id
                )
            ).onSuccess {
                _uiState.update {
                    it.copy(name = "", selectedCategory = null, error = null)
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }
}