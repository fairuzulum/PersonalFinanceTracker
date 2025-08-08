package com.fairuz.finance.domain.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE
)