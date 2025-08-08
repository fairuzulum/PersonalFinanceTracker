package com.fairuz.finance.domain.usecase

import com.fairuz.finance.domain.model.Category
import com.fairuz.finance.domain.repository.CategoryRepository

class AddCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category) = repository.addCategory(category)
}