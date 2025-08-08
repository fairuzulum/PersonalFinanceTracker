package com.fairuz.finance.domain.usecase

import com.fairuz.finance.domain.repository.CategoryRepository

class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String) = repository.deleteCategory(categoryId)
}