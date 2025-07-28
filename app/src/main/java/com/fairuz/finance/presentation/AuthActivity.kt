package com.fairuz.finance.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.fairuz.finance.MainActivity
import com.fairuz.finance.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    // Ganti PIN ini sesuai keinginan Anda.
    // Demi keamanan, pada aplikasi nyata ini seharusnya tidak di-hardcode.
    // Namun sesuai permintaan, kita hardcode untuk penggunaan pribadi.
    private val CORRECT_PIN = "2424"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPinViewListener()
    }

    private fun setupPinViewListener() {
        binding.pinView.addTextChangedListener {
            if (it?.length == 4) {
                // Ketika 6 digit sudah terisi, lakukan pengecekan
                validatePin(it.toString())
            }
        }
    }

    private fun validatePin(enteredPin: String) {
        showLoading(true)

        // Simulasi pengecekan (bisa diganti dengan proses network/database jika perlu)
        binding.root.postDelayed({
            if (enteredPin == CORRECT_PIN) {
                // Jika PIN benar, navigasi ke MainActivity
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                navigateToMainScreen()
            } else {
                // Jika PIN salah, tampilkan pesan error
                showLoading(false)
                Toast.makeText(this, "PIN Salah, coba lagi.", Toast.LENGTH_SHORT).show()
                binding.pinView.text = null // Kosongkan PinView
            }
        }, 500) // Memberi jeda 0.5 detik untuk UX
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Selesaikan AuthActivity agar pengguna tidak bisa kembali ke halaman ini dengan menekan tombol back
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.pinView.isEnabled = !isLoading
    }
}