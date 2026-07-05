package com.example.financialpair.data.repository

import com.example.financialpair.data.dao.TopicDao
import com.example.financialpair.data.entity.Topic

class TopicRepository(
    private val dao: TopicDao
) {
    val topicsWithCategory = dao.observeAllWithCategory()

    val topics = dao.observeAll()

    suspend fun insert(topic: Topic): Result<Long> =
        runCatching {
            dao.insert(topic)
        }
}
