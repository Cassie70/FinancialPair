package com.juni7.financialpair.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TopicWithCategory(
    @Embedded val topic: Topic,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)
