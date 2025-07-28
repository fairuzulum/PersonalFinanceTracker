package com.fairuz.finance.data.repository

import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Import library untuk snapshotFlow
import com.google.firebase.firestore.snapshots

class TransactionRepositoryImpl : TransactionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val transactionCollection = db.collection("transactions")

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        return try {
            transactionCollection.add(transaction).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTransactions(): Flow<Result<List<Transaction>>> {
        return transactionCollection
            // Mengurutkan berdasarkan tanggal, dari yang terbaru
            .orderBy("date", Query.Direction.DESCENDING)
            .snapshots() // Menghasilkan Flow yang emit setiap kali ada perubahan
            .map { snapshot ->
                try {
                    // Konversi setiap dokumen menjadi objek Transaction
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    Result.success(transactions)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
}