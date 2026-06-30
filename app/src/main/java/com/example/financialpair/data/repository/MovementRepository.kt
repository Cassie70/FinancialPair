package com.example.financialpair.data.repository

import com.example.financialpair.data.dao.MovementDao
import com.example.financialpair.data.entity.Movement

class MovementRepository(
    private val dao: MovementDao
){
    val movements = dao.observeAll()

    suspend fun insert(movement: Movement): Result<Unit> =
        runCatching {
            dao.insert(movement)
        }

    suspend fun delete(movement: Movement): Result<Unit> =
        runCatching {
            dao.delete(movement)
        }

}