package com.juni7.financialpair.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.MovementWithTopic
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource
import com.juni7.financialpair.data.model.DateTotal

@Dao
interface MovementDao{
    @Transaction
    @Query("SELECT * FROM movement ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<MovementWithTopic>>

    @Transaction
    @Query("SELECT * FROM movement ORDER BY date DESC, id DESC")
    fun observeAllPaged(): PagingSource<Int, MovementWithTopic>

    @Query("SELECT date, SUM(amount) as total FROM movement GROUP BY date")
    fun observeTotalsByDate(): Flow<List<DateTotal>>

    @Insert
    suspend fun insert(movement: Movement)

    @androidx.room.Update
    suspend fun update(movement: Movement)

    @Query("SELECT * FROM movement WHERE id = :id")
    suspend fun findById(id: Long): Movement?

    @Delete
    suspend fun delete(movement: Movement)
}
