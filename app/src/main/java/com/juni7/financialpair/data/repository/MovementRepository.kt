package com.juni7.financialpair.data.repository

import com.juni7.financialpair.data.dao.MovementDao
import com.juni7.financialpair.data.entity.Movement
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class MovementRepository(
    private val dao: MovementDao
){
    val movements = dao.observeAll()
    val totalsByDate = dao.observeTotalsByDate()

    fun getPagedMovements(): Flow<PagingData<com.juni7.financialpair.data.entity.MovementWithTopic>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.observeAllPaged() }
        ).flow
    }

    suspend fun insert(movement: Movement): Result<Unit> =
        runCatching {
            dao.insert(movement)
        }

    suspend fun update(movement: Movement): Result<Unit> =
        runCatching {
            dao.update(movement)
        }

    suspend fun findById(id: Long): Movement? = dao.findById(id)

    suspend fun delete(movement: Movement): Result<Unit> =
        runCatching {
            dao.delete(movement)
        }

}