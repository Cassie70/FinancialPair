package com.example.financialpair.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.financialpair.data.entity.Movement

@Dao
interface MovementDao{

    @Insert
    suspend fun insert(movement: Movement)

    @Delete
    suspend fun delete(id: Long)
}