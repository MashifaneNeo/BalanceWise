package com.example.balancewise

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balancewise.adapter.ExpenseAdapter
import com.example.balancewise.database.AppDatabase
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseListActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }

    private lateinit var rvExpenses: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var etSearch: EditText

    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        rvExpenses = findViewById(R.id.rvExpenses)
        tvTotal = findViewById(R.id.tvExpenseListTotal)
        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)
        etSearch = findViewById(R.id.etSearch)

        rvExpenses.layoutManager = LinearLayoutManager(this)

        // 📅 Date pickers
        btnStartDate.setOnClickListener { pickDate(btnStartDate) { filterExpenses() } }
        btnEndDate.setOnClickListener { pickDate(btnEndDate) { filterExpenses() } }

        // 🔍 Search
        etSearch.addTextChangedListener {
            loadExpenses(query = it.toString())
        }

        // 💰 Open totals screen
        findViewById<Button>(R.id.btnOpenTotals).setOnClickListener {
            startActivity(Intent(this, CategoryTotalsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    // 🔥 MAIN LOADER
    private fun loadExpenses(
        startDate: String? = null,
        endDate: String? = null,
        query: String? = null
    ) {
        val userId = SessionManager.getUserId(this)

        lifecycleScope.launch {

            val expenses = when {

                // 🔍 + 📅
                !query.isNullOrEmpty() && startDate != null && endDate != null ->
                    db.expenseDao().searchExpensesByDate(userId, query, startDate, endDate)

                // 🔍 only
                !query.isNullOrEmpty() ->
                    db.expenseDao().searchExpenses(userId, query)

                // 📅 only
                startDate != null && endDate != null ->
                    db.expenseDao().getExpensesByDateRange(userId, startDate, endDate)

                // 📋 all
                else ->
                    db.expenseDao().getExpensesForUser(userId)
            }

            runOnUiThread {
                if (!::adapter.isInitialized) {
                    adapter = ExpenseAdapter(expenses)
                    rvExpenses.adapter = adapter
                } else {
                    adapter.updateData(expenses)
                }

                tvTotal.text = "Total: R %.2f".format(expenses.sumOf { it.amount })
            }
        }
    }

    // 📅 Date filter trigger
    private fun filterExpenses() {
        val start = btnStartDate.text.toString()
        val end = btnEndDate.text.toString()
        val query = etSearch.text.toString()

        if (start != "Start Date" && end != "End Date") {
            loadExpenses(start, end, query)
        }
    }

    // 📅 Date picker
    private fun pickDate(target: Button, onDateSet: () -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            target.text = "%02d/%02d/%04d".format(d, m + 1, y)
            onDateSet()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}