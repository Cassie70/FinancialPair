package com.juni7.financialpair.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovementWithTopic(
    @Embedded val movement: Movement,
    @Relation(
        entity = Topic::class,
        parentColumn = "topicId",
        entityColumn = "id"
    )
    val topicWithCategory: TopicWithCategory
) {
    val topic: Topic get() = topicWithCategory.topic
    val category: Category get() = topicWithCategory.category
}
