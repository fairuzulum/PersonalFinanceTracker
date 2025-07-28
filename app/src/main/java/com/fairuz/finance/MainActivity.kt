package com.fairuz.finance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fairuz.finance.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar
        setSupportActionBar(binding.toolbar)

        setupListeners()
        updateUI() // Panggil fungsi untuk update UI awal
    }

    private fun setupListeners() {
        binding.fabAddTransaction.setOnClickListener {
            // Untuk sementara, kita hanya tampilkan Toast.
            // Di langkah selanjutnya, ini akan membuka halaman baru.
            Toast.makeText(this, "Tombol Tambah Transaksi Ditekan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        // Untuk sementara, kita isi dengan data dummy.
        // Nantinya data ini akan berasal dari Firestore.
        binding.tvTotalBalance.text = "Rp 0"
        binding.tvTotalIncome.text = "Rp 0"
        binding.tvTotalExpense.text = "Rp 0"
    }
}