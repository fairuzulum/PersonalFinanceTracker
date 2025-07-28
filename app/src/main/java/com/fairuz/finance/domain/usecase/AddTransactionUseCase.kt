package com.fairuz.finance.domain.usecase

import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.repository.TransactionRepository

class AddTransactionUseCase(
    // Use case bergantung pada abstraksi (interface), bukan implementasi langsung.
    // Inilah inti dari Clean Architecture.
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) = repository.addTransaction(transaction)
}