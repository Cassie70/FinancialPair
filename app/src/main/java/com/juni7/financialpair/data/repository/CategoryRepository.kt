package com.juni7.financialpair.data.repository

import com.juni7.financialpair.data.dao.CategoryDao
import com.juni7.financialpair.data.entity.Category

class CategoryRepository(
    private val dao: CategoryDao
) {
    val categories = dao.observeAll()

    suspend fun insert(category: Category): Result<Unit> =
        runCatching {
            dao.insert(category)
        }
}
