package com.fairuz.finance.domain.usecase

import com.fairuz.finance.domain.repository.TransactionRepository

class GetTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke() = repository.getTransactions()
}