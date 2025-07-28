package com.fairuz.finance.presentation.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fairuz.finance.data.repository.TransactionRepositoryImpl
import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.launch

class AddTransactionViewModel : ViewModel() {

    // Inisialisasi use case. Di proyek besar, ini dilakukan dengan Dependency Injection.
    private val repository = TransactionRepositoryImpl()
    private val addTransactionUseCase = AddTransactionUseCase(repository)

    // LiveData untuk mengkomunikasikan status ke Activity
    private val _addTransactionState = MutableLiveData<AddTransactionState>()
    val addTransactionState: LiveData<AddTransactionState> = _addTransactionState

    fun saveTransaction(transaction: Transaction) {
        // Jalankan coroutine di scope milik ViewModel
        viewModelScope.launch {
            _addTransactionState.value = AddTransactionState.Loading
            val result = addTransactionUseCase(transaction)
            result.onSuccess {
                _addTransactionState.value = AddTransactionState.Success("Transaksi berhasil disimpan!")
            }.onFailure {
                _addTransactionState.value = AddTransactionState.Error(it.message ?: "Terjadi kesalahan")
            }
        }
    }
}

// Sealed class untuk merepresentasikan state dari proses penambahan transaksi
sealed class AddTransactionState {
    object Loading : AddTransactionState()
    data class Success(val message: String) : AddTransactionState()
    data class Error(val message: String) : AddTransactionState()
}