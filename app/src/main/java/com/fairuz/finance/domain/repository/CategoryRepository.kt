package com.fairuz.finance.domain.repository

import com.fairuz.finance.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(type: String): Flow<Result<List<Category>>>
    suspend fun addCategory(category: Category): Result<Unit>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
}