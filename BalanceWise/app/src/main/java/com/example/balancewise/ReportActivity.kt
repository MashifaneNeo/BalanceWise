package com.example.balancewise

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getInstance(this) }
    private lateinit var tvReportContent: TextView
    private lateinit var btnShareReport: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        tvReportContent = findViewById(R.id.tvReportContent)
        btnShareReport = findViewById(R.id.btnShareReport)

        generateReport()

        btnShareReport.setOnClickListener {
            val report = tvReportContent.text.toString()
            if (report.isNotEmpty() && report != "Loading report..." && report != "Generating report...") {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "BalanceWise Expense Report")
                    putExtra(Intent.EXTRA_TEXT, report)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Report via"))
            } else {
                Toast.makeText(this, "Please wait for report to finish loading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateReport() {
        tvReportContent.text = "Generating report..."

        val userId = SessionManager.getUserId(this)

        if (userId == -1) {
            tvReportContent.text = "Error: Please login again"
            return
        }

        lifecycleScope.launch {
            try {
                val expenses = db.expenseDao().getExpensesForUser(userId)
                val categories = db.categoryDao().getCategoriesForUser(userId)
                val goal = db.goalDao().getGoalForUser(userId)

                val report = buildSimpleReport(expenses, categories, goal)

                runOnUiThread {
                    tvReportContent.text = report
                }
            } catch (e: Exception) {
                runOnUiThread {
                    tvReportContent.text = "Error: ${e.message}\n\nPlease try again"
                }
            }
        }
    }

    private fun buildSimpleReport(expenses: List<com.example.balancewise.database.Expense>,
                                  categories: List<com.example.balancewise.database.Category>,
                                  goal: com.example.balancewise.database.Goal?): String {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val reportDate = dateFormat.format(Date())

        val totalSpent = expenses.sumOf { it.amount }

        val report = StringBuilder()

        report.appendLine("=".repeat(40))
        report.appendLine("BALANCEWISE REPORT")
        report.appendLine("=".repeat(40))
        report.appendLine("Date: $reportDate")
        report.appendLine()
        report.appendLine("TOTAL SPENT: R ${String.format("%.2f", totalSpent)}")
        report.appendLine("TRANSACTIONS: ${expenses.size}")
        report.appendLine()

        if (goal != null) {
            report.appendLine("GOALS:")
            report.appendLine("  Min: R ${String.format("%.2f", goal.minGoal)}")
            report.appendLine("  Max: R ${String.format("%.2f", goal.maxGoal)}")
            if (totalSpent > goal.maxGoal) {
                report.appendLine("  STATUS: OVER BUDGET!")
            } else if (totalSpent < goal.minGoal) {
                report.appendLine("  STATUS: Below target")
            } else {
                report.appendLine("  STATUS: On track")
            }
            report.appendLine()
        }

        report.appendLine("CATEGORIES:")
        categories.forEach { category ->
            val categoryTotal = expenses.filter { it.categoryId == category.id }.sumOf { it.amount }
            if (categoryTotal > 0) {
                val percentage = (categoryTotal / totalSpent) * 100
                report.appendLine("  ${category.name}: R ${String.format("%.2f", categoryTotal)} (${String.format("%.1f", percentage)}%)")
            }
        }

        report.appendLine()
        report.appendLine("=".repeat(40))

        return report.toString()
    }
}