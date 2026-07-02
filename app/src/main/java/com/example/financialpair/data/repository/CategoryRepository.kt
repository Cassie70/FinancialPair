package com.example.financialpair.data.repository

import com.example.financialpair.data.dao.CategoryDao
import com.example.financialpair.data.entity.Category

class CategoryRepository(
    private val dao: CategoryDao
) {
    val categories = dao.observeAll()

    suspend fun insert(category: Category): Result<Unit> =
        runCatching {
            dao.insert(category)
        }
}
