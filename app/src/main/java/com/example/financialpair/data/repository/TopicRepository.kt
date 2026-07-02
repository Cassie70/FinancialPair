package com.example.financialpair.data.repository

import com.example.financialpair.data.dao.TopicDao
import com.example.financialpair.data.entity.Topic

class TopicRepository(
    private val dao: TopicDao
) {
    val topicsWithCategory = dao.observeAllWithCategory()

    suspend fun insert(topic: Topic): Result<Unit> =
        runCatching {
            dao.insert(topic)
        }
}
