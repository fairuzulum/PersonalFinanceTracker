package com.fairuz.finance.presentation

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fairuz.finance.R
import com.fairuz.finance.databinding.ActivityAddTransactionBinding
import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.model.TransactionType
import com.fairuz.finance.presentation.add.AddTransactionState
import com.fairuz.finance.presentation.add.AddTransactionViewModel

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: AddTransactionViewModel by viewModels()

    private var selectedType: TransactionType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupTypeToggle()
        setupCategorySpinner()
        setupSaveButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTypeToggle() {
        // Set Pengeluaran sebagai default
        binding.toggleType.check(R.id.btnExpense)
        selectedType = TransactionType.EXPENSE

        binding.toggleType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                selectedType = when (checkedId) {
                    R.id.btnIncome -> TransactionType.INCOME
                    R.id.btnExpense -> TransactionType.EXPENSE
                    else -> null
                }
            }
        }
    }

    private fun setupCategorySpinner() {
        // Daftar kategori bisa diperluas atau diambil dari database nantinya
        val categories = listOf("Makanan", "Transportasi", "Gaji", "Tagihan", "Hiburan", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            val description = binding.etDescription.text.toString()
            val category = binding.actCategory.text.toString()

            // Validasi input
            if (selectedType == null || amount == null || description.isBlank() || category.isBlank()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                description = description,
                amount = if (selectedType == TransactionType.INCOME) amount else -amount, // Pengeluaran jadi negatif
                type = selectedType!!,
                category = category
            )
            viewModel.saveTransaction(transaction)
        }
    }

    private fun observeViewModel() {
        viewModel.addTransactionState.observe(this) { state ->
            when (state) {
                is AddTransactionState.Loading -> {
                    // Bisa tambahkan ProgressBar di sini
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = "Menyimpan..."
                }
                is AddTransactionState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish() // Tutup activity jika berhasil
                }
                is AddTransactionState.Error -> {
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Simpan Transaksi"
                }
            }
        }
    }
}