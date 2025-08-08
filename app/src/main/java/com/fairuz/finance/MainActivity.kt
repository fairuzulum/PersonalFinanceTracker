package com.fairuz.finance

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairuz.finance.databinding.ActivityMainBinding
import com.fairuz.finance.presentation.AddTransactionActivity
import com.fairuz.finance.presentation.category.CategoryActivity
import com.fairuz.finance.presentation.dashboard.MainUiState
import com.fairuz.finance.presentation.dashboard.MainViewModel
import com.fairuz.finance.presentation.dashboard.TransactionAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manage_categories -> {
                startActivity(Intent(this, CategoryActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupListeners() {
        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MainUiState.Loading -> {
                        // Anda bisa menambahkan ProgressBar di sini
                    }
                    is MainUiState.Success -> {
                        handleSuccessState(state)
                    }
                    is MainUiState.Error -> {
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun handleSuccessState(state: MainUiState.Success) {
        // Update summary cards
        binding.tvTotalBalance.text = formatRupiah(state.totalBalance)
        binding.tvTotalIncome.text = formatRupiah(state.totalIncome)
        binding.tvTotalExpense.text = formatRupiah(state.totalExpense)

        // Update RecyclerView
        transactionAdapter.submitList(state.transactions)

        // Handle empty state
        if (state.transactions.isEmpty()) {
            binding.rvTransactions.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvTransactions.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}