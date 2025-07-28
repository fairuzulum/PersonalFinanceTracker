package com.fairuz.finance.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    // Anotasi @DocumentId memungkinkan Firestore untuk secara otomatis
    // mengisi properti ini dengan ID dokumen saat data dibaca.
    @DocumentId
    val id: String = "",

    val description: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",

    // Anotasi @ServerTimestamp akan membuat Firebase mengisi field ini
    // dengan waktu server saat dokumen dibuat. Ini lebih akurat daripada waktu perangkat.
    @ServerTimestamp
    val date: Date? = null
)