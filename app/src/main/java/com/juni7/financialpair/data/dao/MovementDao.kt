package com.juni7.financialpair.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.MovementWithTopic
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao{
    @Transaction
    @Query("SELECT * FROM movement ORDER BY id DESC")
    fun observeAll(): Flow<List<MovementWithTopic>>

    @Insert
    suspend fun insert(movement: Movement)

    @Delete
    suspend fun delete(movement: Movement)
}
