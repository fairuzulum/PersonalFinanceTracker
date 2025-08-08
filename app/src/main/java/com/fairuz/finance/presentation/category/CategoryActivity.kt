package com.fairuz.finance.presentation.category

import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairuz.finance.R
import com.fairuz.finance.databinding.ActivityCategoryBinding
import com.fairuz.finance.domain.model.Category
import com.fairuz.finance.domain.model.TransactionType
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private var selectedType: TransactionType = TransactionType.EXPENSE // Default ke pengeluaran

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        setupTypeToggle() // Mengatur toggle
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            AlertDialog.Builder(this)
                .setTitle("Hapus Kategori")
                .setMessage("Apakah Anda yakin ingin menghapus kategori '${category.name}'?")
                .setPositiveButton("Hapus") { _, _ ->
                    viewModel.deleteCategory(category.id)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(this@CategoryActivity)
        }
    }

    private fun setupListeners() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun setupTypeToggle() {
        // Set Pengeluaran sebagai default saat pertama kali dibuka
        binding.toggleCategoryType.check(R.id.btnExpenseCategory)
        viewModel.getCategories(TransactionType.EXPENSE.name) // Ambil data kategori pengeluaran

        binding.toggleCategoryType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedType = when (checkedId) {
                    R.id.btnIncomeCategory -> TransactionType.INCOME
                    R.id.btnExpenseCategory -> TransactionType.EXPENSE
                    else -> TransactionType.EXPENSE
                }
                // Ambil data kategori sesuai tipe yang dipilih
                viewModel.getCategories(selectedType.name)
            }
        }
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categoryState.collect { state ->
                when (state) {
                    is CategoryState.Loading -> {
                        // Tampilkan loading jika perlu
                    }
                    is CategoryState.Success -> {
                        categoryAdapter.submitList(state.categories)
                    }
                    is CategoryState.Error -> {
                        // Tampilkan pesan error jika perlu
                    }
                }
            }
        }
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(this)
        val typeString = if (selectedType == TransactionType.INCOME) "Pemasukan" else "Pengeluaran"
        AlertDialog.Builder(this)
            .setTitle("Tambah Kategori $typeString")
            .setView(editText)
            .setPositiveButton("Tambah") { _, _ ->
                val categoryName = editText.text.toString()
                if (categoryName.isNotBlank()) {
                    // Buat kategori baru sesuai tipe yang sedang dipilih
                    val category = Category(name = categoryName, type = selectedType)
                    viewModel.addCategory(category)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}