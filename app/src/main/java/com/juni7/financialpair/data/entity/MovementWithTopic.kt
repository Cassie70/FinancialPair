package com.juni7.financialpair.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovementWithTopic(
    @Embedded val movement: Movement,
    @Relation(
        parentColumn = "topicId",
        entityColumn = "id"
    )
    val topic: Topic
)
