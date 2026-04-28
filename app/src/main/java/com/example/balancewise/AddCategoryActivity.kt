package com.example.balancewise

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import com.example.balancewise.database.Category
import kotlinx.coroutines.launch

class AddCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val etCategoryName = findViewById<EditText>(R.id.etCategoryName)
        val btnSaveCategory = findViewById<Button>(R.id.btnSaveCategory)
        val db = AppDatabase.getInstance(this)
        val userId = SessionManager.getUserId(this)

        btnSaveCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    db.categoryDao().insertCategory(Category(name = name, userId = userId))
                    runOnUiThread {
                        Toast.makeText(this@AddCategoryActivity, "Category saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}