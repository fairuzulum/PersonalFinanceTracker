package com.fairuz.finance.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fairuz.finance.data.repository.CategoryRepositoryImpl
import com.fairuz.finance.domain.model.Category
import com.fairuz.finance.domain.usecase.AddCategoryUseCase
import com.fairuz.finance.domain.usecase.DeleteCategoryUseCase
import com.fairuz.finance.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepositoryImpl()
    private val getCategoriesUseCase = GetCategoriesUseCase(repository)
    private val addCategoryUseCase = AddCategoryUseCase(repository)
    private val deleteCategoryUseCase = DeleteCategoryUseCase(repository)

    private val _categoryState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoryState: StateFlow<CategoryState> = _categoryState

    fun getCategories(type: String) {
        viewModelScope.launch {
            getCategoriesUseCase(type)
                .catch { e -> _categoryState.value = CategoryState.Error(e.message ?: "An error occurred") }
                .collect { result ->
                    result.onSuccess { categories ->
                        _categoryState.value = CategoryState.Success(categories)
                    }.onFailure { e ->
                        _categoryState.value = CategoryState.Error(e.message ?: "An error occurred")
                    }
                }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            addCategoryUseCase(category)
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            deleteCategoryUseCase(categoryId)
        }
    }
}

sealed class CategoryState {
    object Loading : CategoryState()
    data class Success(val categories: List<Category>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}