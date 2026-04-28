package com.example.balancewise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val db = AppDatabase.getInstance(this)

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