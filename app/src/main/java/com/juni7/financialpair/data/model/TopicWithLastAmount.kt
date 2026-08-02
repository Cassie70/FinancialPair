package com.juni7.financialpair.data.model

import androidx.room.Embedded
import com.juni7.financialpair.data.entity.Topic

data class TopicWithLastAmount(
    @Embedded val topic: Topic,
    val lastAmount: Int?
)
