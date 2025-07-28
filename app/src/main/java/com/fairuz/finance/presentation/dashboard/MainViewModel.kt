package com.fairuz.finance.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fairuz.finance.data.repository.TransactionRepositoryImpl
import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.model.TransactionType
import com.fairuz.finance.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = TransactionRepositoryImpl()
    private val getTransactionsUseCase = GetTransactionsUseCase(repository)

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase()
                .onStart { _uiState.value = MainUiState.Loading }
                .catch { e -> _uiState.value = MainUiState.Error(e.message ?: "An error occurred") }
                .collect { result ->
                    result.onSuccess { transactions ->
                        processTransactions(transactions)
                    }.onFailure { e ->
                        _uiState.value = MainUiState.Error(e.message ?: "An error occurred")
                    }
                }
        }
    }

    private fun processTransactions(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { kotlin.math.abs(it.amount) }
        val totalBalance = totalIncome - totalExpense

        _uiState.value = MainUiState.Success(
            transactions = transactions,
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense
        )
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(
        val transactions: List<Transaction>,
        val totalBalance: Double,
        val totalIncome: Double,
        val totalExpense: Double
    ) : MainUiState()
    data class Error(val message: String) : MainUiState()
}