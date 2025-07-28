package com.fairuz.finance.domain.repository

import com.fairuz.finance.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction): Result<Unit>

    // Fungsi baru untuk mendapatkan aliran data transaksi
    fun getTransactions(): Flow<Result<List<Transaction>>>
}