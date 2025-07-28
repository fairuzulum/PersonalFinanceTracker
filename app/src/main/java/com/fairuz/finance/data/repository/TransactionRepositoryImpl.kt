package com.fairuz.finance.data.repository

import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransactionRepositoryImpl : TransactionRepository {

    // Mendapatkan instance Firestore
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            // Menambahkan dokumen baru ke koleksi "transactions"
            db.collection("transactions").add(transaction).await()
            Result.success(Unit) // Mengembalikan hasil sukses
        } catch (e: Exception) {
            // Jika terjadi error, kembalikan hasil gagal dengan exception-nya
            Result.failure(e)
        }
    }
}