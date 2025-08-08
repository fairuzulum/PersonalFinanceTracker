package com.fairuz.finance.domain.usecase

import com.fairuz.finance.domain.repository.CategoryRepository

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    operator fun invoke(type: String) = repository.getCategories(type)
}