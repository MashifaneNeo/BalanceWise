package com.example.balancewise

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balancewise.adapter.CategoryTotalAdapter
import com.example.balancewise.database.AppDatabase
import com.example.balancewise.model.CategoryTotalItem
import kotlinx.coroutines.launch
import java.util.Calendar

class CategoryTotalsActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var rvTotals: RecyclerView
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_totals)

        rvTotals = findViewById(R.id.rvCategoryTotals)
        rvTotals.layoutManager = LinearLayoutManager(this)
        btnStart = findViewById(R.id.btnTotalsStartDate)
        btnEnd = findViewById(R.id.btnTotalsEndDate)

        btnStart.setOnClickListener { pickDate(btnStart) { filterTotals() } }
        btnEnd.setOnClickListener { pickDate(btnEnd) { filterTotals() } }
    }

    override fun onResume() {
        super.onResume()
        loadTotals()
    }

    private fun loadTotals(startDate: String? = null, endDate: String? = null) {
        val userId = SessionManager.getUserId(this)
        lifecycleScope.launch {
            val categories = db.categoryDao().getCategoriesForUser(userId)
            val expenses = if (startDate != null && endDate != null)
                db.expenseDao().getExpensesByDateRange(userId, startDate, endDate)
            else
                db.expenseDao().getExpensesForUser(userId)

            val totals = categories.map { cat ->
                CategoryTotalItem(cat.name, expenses.filter { it.categoryId == cat.id }.sumOf { it.amount })
            }
            runOnUiThread { rvTotals.adapter = CategoryTotalAdapter(totals) }
        }
    }

    private fun filterTotals() {
        val start = btnStart.text.toString()
        val end = btnEnd.text.toString()
        if (start != "Start Date" && end != "End Date") loadTotals(start, end)
    }

    private fun pickDate(target: Button, onDateSet: () -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            target.text = "%02d/%02d/%04d".format(d, m + 1, y)
            onDateSet()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}