package com.example.balancewise

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.balancewise.database.AppDatabase
import com.example.balancewise.database.Category
import com.example.balancewise.database.Expense
import kotlinx.coroutines.launch
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private var selectedPhotoUri: Uri? = null
    private var categoryList: List<Category> = emptyList()
    private val db by lazy { AppDatabase.getInstance(this) }
    private val cal get() = Calendar.getInstance()

    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnDate: Button
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var spinnerCategory: Spinner
    private lateinit var imgReceiptPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etAmount = findViewById(R.id.etAmount)
        etDescription = findViewById(R.id.etDescription)
        btnDate = findViewById(R.id.btnDate)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        imgReceiptPreview = findViewById(R.id.imgReceiptPreview)

        loadCategories()
        setupDateTimePickers()
        setupImagePicker()

        findViewById<Button>(R.id.btnSaveExpense).setOnClickListener { saveExpense() }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            categoryList = db.categoryDao().getCategoriesForUser(SessionManager.getUserId(this@AddExpenseActivity))
            runOnUiThread {
                spinnerCategory.adapter = ArrayAdapter(
                    this@AddExpenseActivity,
                    android.R.layout.simple_spinner_item,
                    categoryList.map { it.name }
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            }
        }
    }

    private fun setupDateTimePickers() {
        btnDate.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                btnDate.text = "%02d/%02d/%04d".format(d, m + 1, y)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        btnStartTime.setOnClickListener { showTimePicker(btnStartTime) }
        btnEndTime.setOnClickListener { showTimePicker(btnEndTime) }
    }

    private fun setupImagePicker() {
        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedPhotoUri = uri
            if (uri != null) imgReceiptPreview.setImageURI(uri)
        }
        findViewById<Button>(R.id.btnPhoto).setOnClickListener { imagePicker.launch("image/*") }
    }

    private fun saveExpense() {
        val amount = etAmount.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (amount.isBlank() || description.isBlank() ||
            btnDate.text == "Select Date" || btnStartTime.text == "Start Time" ||
            btnEndTime.text == "End Time" || categoryList.isEmpty()) {
            Toast.makeText(this, "Complete all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            db.expenseDao().insertExpense(
                Expense(
                    amount = amount.toDouble(),
                    date = btnDate.text.toString(),
                    startTime = btnStartTime.text.toString(),
                    endTime = btnEndTime.text.toString(),
                    description = description,
                    categoryId = categoryList[spinnerCategory.selectedItemPosition].id,
                    userId = SessionManager.getUserId(this@AddExpenseActivity),
                    photoUri = selectedPhotoUri?.toString()
                )
            )
            runOnUiThread {
                Toast.makeText(this@AddExpenseActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showTimePicker(target: Button) {
        TimePickerDialog(this, { _, h, m ->
            target.text = "%02d:%02d".format(h, m)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }
}