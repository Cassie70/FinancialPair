package com.juni7.financialpair.ui.screens.movements

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.MovementWithTopic
import com.juni7.financialpair.data.entity.Topic
import com.juni7.financialpair.data.repository.MovementRepository
import com.juni7.financialpair.data.repository.TopicRepository
import com.juni7.financialpair.util.FuzzyMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.math.roundToInt

class MovementsScreenViewModel(
    private val repository: MovementRepository,
    private val topicRepository: TopicRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(MovementsScreenState())
    val uiState = _uiState.asStateFlow()

    private var allTopics: List<Topic> = emptyList()
    private val inProgressFetches = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            repository.movements.collect { movements ->
                _uiState.update {
                    it.copy(
                        movements = movements
                    )
                }
                fetchMissingLogos(movements)
            }
        }

        viewModelScope.launch {
            topicRepository.topics.collect { topics ->
                allTopics = topics
            }
        }
    }

    private fun fetchMissingLogos(movements: List<MovementWithTopic>) {
        val currentUrls = _uiState.value.logoUrls
        val uniqueTopics = movements.map { it.topicWithCategory }.distinctBy { it.topic.name }

        uniqueTopics.forEach { topicWithCategory ->
            val topic = topicWithCategory.topic
            val category = topicWithCategory.category
            
            // Use cached URL from database if available and not already in state
            if (!currentUrls.containsKey(topic.name) && topic.logoUrl != null) {
                _uiState.update {
                    it.copy(logoUrls = it.logoUrls + (topic.name to topic.logoUrl))
                }
            }

            // Only fetch from Firebase if we don't have a URL in state
            val stateUrls = _uiState.value.logoUrls
            if (!stateUrls.containsKey(topic.name) && !inProgressFetches.contains(topic.name)) {
                inProgressFetches.add(topic.name)
                Log.d("MovementsVM", "Checking Firebase for topic: ${topic.name}")
                viewModelScope.launch {
                    try {
                        val topicPath = "logos/${topic.name.trim().lowercase().replace(' ', '-')}.svg"
                        Log.d("MovementsVM", "Attempting to fetch URL from Firebase for path: $topicPath")
                        
                        val url = try {
                            Firebase.storage.reference.child(topicPath).downloadUrl.await().toString()
                        } catch (e: Exception) {
                            Log.d("MovementsVM", "Topic logo not found, trying category fallback: ${category.name}")
                            val categoryPath = "logos/${category.name.trim().lowercase().replace(' ', '-')}.svg"
                            Firebase.storage.reference.child(categoryPath).downloadUrl.await().toString()
                        }

                        Log.d("MovementsVM", "Successfully fetched logo for ${topic.name}: $url")

                        // Update state
                        _uiState.update {
                            it.copy(logoUrls = it.logoUrls + (topic.name to url))
                        }

                        // Persist to database
                        topicRepository.updateLogoUrl(topic.id, url)

                    } catch (e: Exception) {
                        Log.e("MovementsVM", "Exception during fetch for ${topic.name}: ${e.message}", e)
                    } finally {
                        inProgressFetches.remove(topic.name)
                    }
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
        val suggestions = if (value.length >= 2) {
            val query = value.lowercase()
            allTopics.filter { 
                it.name.lowercase().contains(query) 
            }.take(5)
        } else {
            emptyList()
        }

        _uiState.update {
            it.copy(
                description = value,
                suggestedTopics = suggestions,
                hasDescriptionError = !validateDescription(value)
            )
        }
    }

    fun onTopicSelected(topic: Topic) {
        _uiState.update {
            it.copy(
                description = topic.name,
                suggestedTopics = emptyList()
            )
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

        // Buscamos el tema con la mayor coincidencia (menor distancia)
        val matchedTopic = FuzzyMatcher.findBestMatchedTopic(_uiState.value.description, allTopics)

        val today = LocalDate.now()
        val date = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth

        viewModelScope.launch {
            // Si no hay coincidencia razonable, se inserta un nuevo Topic
            val finalTopicId = matchedTopic?.id ?: topicRepository.insert(
                Topic(
                    name = _uiState.value.description,
                    categoryId = 0
                )
            ).getOrNull()?.toInt() ?: 0

            repository.insert(
                Movement(
                    description = _uiState.value.description,
                    amount = (_uiState.value.amount.toDouble() * 100).roundToInt(),
                    date = date,
                    topicId = finalTopicId
                )
            )
                .onSuccess {
                    _uiState.update { it.copy(description = "", amount = "", suggestedTopics = emptyList()) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }
}
