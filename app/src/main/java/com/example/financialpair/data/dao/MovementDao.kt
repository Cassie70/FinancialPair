package com.example.financialpair.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.financialpair.data.entity.Movement
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao{

    @Query("SELECT * FROM movement ORDER BY id DESC")
    fun observeAll(): Flow<List<Movement>>

    @Insert
    suspend fun insert(movement: Movement)

    @Delete
    suspend fun delete(movement: Movement)
}