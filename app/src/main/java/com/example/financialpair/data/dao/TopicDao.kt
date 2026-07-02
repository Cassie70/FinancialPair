package com.example.financialpair.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financialpair.data.entity.Topic
import com.example.financialpair.data.entity.TopicWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(topic: Topic)

    @Query("SELECT topic.*, category.name as categoryName FROM topic LEFT JOIN category ON topic.categoryId = category.id ORDER BY topic.categoryId ASC")
    fun observeAllWithCategory(): Flow<List<TopicWithCategory>>
}
