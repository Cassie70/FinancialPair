package com.juni7.financialpair.data.repository

import com.juni7.financialpair.data.dao.TopicDao
import com.juni7.financialpair.data.entity.Topic

class TopicRepository(
    private val dao: TopicDao
) {
    val topicsWithCategory = dao.observeAllWithCategory()

    val topics = dao.observeAll()

    suspend fun insert(topic: Topic): Result<Long> =
        runCatching {
            dao.insert(topic)
        }

    suspend fun update(topic: Topic): Result<Unit> =
        runCatching {
            dao.update(topic)
        }

    suspend fun updateLogoUrl(topicId: Int, logoUrl: String): Result<Unit> =
        runCatching {
            dao.updateLogoUrl(topicId, logoUrl)
        }

    suspend fun delete(topic: Topic): Result<Unit> =
        runCatching {
            dao.delete(topic)
        }
}
