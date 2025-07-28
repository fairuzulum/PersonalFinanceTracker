package com.fairuz.finance.domain.repository

import com.fairuz.finance.domain.model.Transaction

interface TransactionRepository {
    // Fungsi suspend menandakan ini adalah operasi asynchronous (coroutine)
    // yang tidak akan memblokir thread utama.
    // Result<Unit> adalah cara modern untuk menangani sukses atau gagal.
    suspend fun addTransaction(transaction: Transaction): Result<Unit>
}