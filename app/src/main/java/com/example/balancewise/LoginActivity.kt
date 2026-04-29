package com.example.balancewise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import com.example.balancewise.database.User
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val switchDarkMode = findViewById<SwitchCompat>(R.id.switchDarkMode)
        val db = AppDatabase.getInstance(this)

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        switchDarkMode.isChecked = sharedPreferences.getBoolean("isDarkMode", false)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var user = db.userDao().login(username, password)

                if (user == null) {
                    val existing = db.userDao().getUserByUsername(username)
                    if (existing == null) {
                        db.userDao().insertUser(User(username = username, password = password))
                        user = db.userDao().login(username, password)
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                }

                user?.let {
                    SessionManager.saveUserId(this@LoginActivity, it.id)
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }
}