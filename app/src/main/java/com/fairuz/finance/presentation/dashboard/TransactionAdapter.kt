package com.fairuz.finance.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fairuz.finance.R
import com.fairuz.finance.databinding.ItemTransactionBinding
import com.fairuz.finance.domain.model.Transaction
import com.fairuz.finance.domain.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.tvDescription.text = transaction.description
            binding.tvCategory.text = transaction.category

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            formatRupiah.maximumFractionDigits = 0 // Menghilangkan desimal

            val (formattedAmount, color) = if (transaction.type == TransactionType.INCOME) {
                "+ ${formatRupiah.format(transaction.amount)}" to R.color.income_color
            } else {
                // amount pengeluaran sudah negatif, jadi kita pakai abs() untuk format
                "- ${formatRupiah.format(kotlin.math.abs(transaction.amount))}" to R.color.expense_color
            }

            binding.tvAmount.text = formattedAmount
            binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, color))
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}