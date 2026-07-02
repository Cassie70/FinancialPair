package com.example.financialpair.data.entity

import androidx.room.Embedded

data class TopicWithCategory(
    @Embedded val topic: Topic,
    val categoryName: String
)
