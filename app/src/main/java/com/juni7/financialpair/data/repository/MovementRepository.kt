package com.juni7.financialpair.data.repository

import com.juni7.financialpair.data.dao.MovementDao
import com.juni7.financialpair.data.entity.Movement

class MovementRepository(
    private val dao: MovementDao
){
    val movements = dao.observeAll()

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