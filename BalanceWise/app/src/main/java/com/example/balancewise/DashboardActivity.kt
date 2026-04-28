package com.example.balancewise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotalSpent: TextView
    private lateinit var tvMinGoal: TextView
    private lateinit var tvMaxGoal: TextView
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvMinGoal = findViewById(R.id.tvMinGoal)
        tvMaxGoal = findViewById(R.id.tvMaxGoal)

        findViewById<Button>(R.id.btnAddCategory).setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        findViewById<Button>(R.id.btnGoals).setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }
        findViewById<Button>(R.id.btnExpenses).setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
        findViewById<Button>(R.id.btnCategoryTotals).setOnClickListener {
            startActivity(Intent(this, CategoryTotalsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        val userId = SessionManager.getUserId(this)
        lifecycleScope.launch {
            val expenses = db.expenseDao().getExpensesForUser(userId)
            val total = expenses.sumOf { it.amount }
            val goal = db.goalDao().getGoalForUser(userId)

            runOnUiThread {
                tvTotalSpent.text = "R %.2f".format(total)
                tvMinGoal.text = "Min Goal: R %.2f".format(goal?.minGoal ?: 500.0)
                tvMaxGoal.text = "Max Goal: R %.2f".format(goal?.maxGoal ?: 5000.0)
            }
        }
    }
}