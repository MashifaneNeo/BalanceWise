package com.example.balancewise

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var searchView: SearchView
    private lateinit var adapter: ExpenseAdapter

    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        rvExpenses = findViewById(R.id.rvExpenses)
        tvTotal = findViewById(R.id.tvExpenseListTotal)
        btnStartDate = findViewById(R.id.btnStartDate)
        btnEndDate = findViewById(R.id.btnEndDate)
        searchView = findViewById(R.id.searchExpenses)

        adapter = ExpenseAdapter(emptyList())
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

        btnStartDate.setOnClickListener { pickDate(btnStartDate) { loadExpenses() } }
        btnEndDate.setOnClickListener { pickDate(btnEndDate) { loadExpenses() } }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query?.trim() ?: ""
                loadExpenses()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText?.trim() ?: ""
                loadExpenses()
                return true
            }
        })

        findViewById<Button>(R.id.btnOpenTotals).setOnClickListener {
            startActivity(Intent(this, CategoryTotalsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {
        val userId = SessionManager.getUserId(this)
        val start = btnStartDate.text.toString()
        val end = btnEndDate.text.toString()
        val hasDate = start != "Start Date" && end != "End Date"
        val hasQuery = currentQuery.isNotEmpty()

        lifecycleScope.launch {
            val expenses = when {
                hasQuery && hasDate -> db.expenseDao().searchExpensesByDate(userId, currentQuery, start, end)
                hasQuery -> db.expenseDao().searchExpenses(userId, currentQuery)
                hasDate -> db.expenseDao().getExpensesByDateRange(userId, start, end)
                else -> db.expenseDao().getExpensesForUser(userId)
            }

            runOnUiThread {
                adapter.updateData(expenses)
                tvTotal.text = "Total: R %.2f".format(expenses.sumOf { it.amount })
            }
        }
    }

    private fun pickDate(target: Button, onDateSet: () -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            target.text = "%02d/%02d/%04d".format(d, m + 1, y)
            onDateSet()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}