package com.fairuz.finance.data.repository

import android.util.Log
import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

// Buat data class untuk mengirim data ke Apps Script
@Serializable
data class SheetRow(
    val key: String,
    val date: String,
    val type: String,
    val category: String,
    val description: String,
    val amount: Double
)

class TransactionRepositoryImpl : TransactionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val transactionCollection = db.collection("transactions")

    // Siapkan Ktor HTTP Client
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun addTransaction(transaction: Transaction): Result<Unit> {
        // 1. Simpan ke Firestore seperti biasa
        val firestoreResult = try {
            transactionCollection.add(transaction).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

        // 2. Jika Firestore berhasil, coba sinkronisasi ke Google Sheet
        if (firestoreResult.isSuccess) {
            // Jalankan di background thread
            withContext(Dispatchers.IO) {
                syncToGoogleSheet(transaction)
            }
        }

        return firestoreResult
    }

    // Fungsi baru untuk sinkronisasi
    private suspend fun syncToGoogleSheet(transaction: Transaction) {
        // Ganti dengan URL Web App Anda dari Google Apps Script
        val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbzou9-BZxyyGUW5JLCv3sSc4z7pyBhL94O0JxujQtqC8UrXLduTPB-tfxdwplVAr_fx/exec"
        // Ganti dengan Secret Key yang Anda atur di skrip
        val SECRET_KEY = "RAHASIA12345"

        // Format tanggal agar sesuai
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = transaction.date?.let { sdf.format(it) } ?: sdf.format(java.util.Date())

        // Siapkan data untuk dikirim
        val sheetRow = SheetRow(
            key = SECRET_KEY,
            date = formattedDate,
            type = transaction.type.name,
            category = transaction.category,
            description = transaction.description,
            amount = transaction.amount
        )

        try {
            // Kirim data menggunakan HTTP POST
            httpClient.post(SCRIPT_URL) {
                contentType(ContentType.Application.Json)
                setBody(sheetRow)
            }
            Log.d("SheetSync", "Successfully synced to Google Sheet.")
        } catch (e: Exception) {
            // Jika gagal, cukup catat error-nya. Jangan hentikan alur aplikasi.
            Log.e("SheetSync", "Failed to sync to Google Sheet: ${e.message}")
        }
    }

    override fun getTransactions(): Flow<Result<List<Transaction>>> {
        return transactionCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                try {
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    Result.success(transactions)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
}