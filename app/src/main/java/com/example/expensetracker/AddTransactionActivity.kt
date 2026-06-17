package com.example.expensetracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.database.ExpenseDatabase
import com.example.expensetracker.database.Transaction
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var tvDate: TextView

    private val db by lazy { ExpenseDatabase.getDatabase(this) }
    private val dao by lazy { db.transactionDao() }

    private val incomeCategories = listOf("Salary", "Freelance", "Investment", "Gift", "Business", "Other")
    private val expenseCategories = listOf("Food", "Transport", "Shopping", "Bills", "Health", "Entertainment", "Education", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initViews()
        setupCategorySpinner(expenseCategories) // default to expense
        setTodayDate()

        rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbIncome -> setupCategorySpinner(incomeCategories)
                R.id.rbExpense -> setupCategorySpinner(expenseCategories)
            }
        }

        btnSave.setOnClickListener { saveTransaction() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun initViews() {
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        etNote = findViewById(R.id.etNote)
        rgType = findViewById(R.id.rgType)
        rbIncome = findViewById(R.id.rbIncome)
        rbExpense = findViewById(R.id.rbExpense)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        tvDate = findViewById(R.id.tvDate)
    }

    private fun setupCategorySpinner(categories: List<String>) {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter
    }

    private fun setTodayDate() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())
    }

    private fun saveTransaction() {
        val title = etTitle.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()
        val note = etNote.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val type = if (rbIncome.isChecked) "income" else "expense"
        val date = tvDate.text.toString()

        if (title.isEmpty()) {
            etTitle.error = "Please enter a title"
            return
        }
        if (amountStr.isEmpty()) {
            etAmount.error = "Please enter an amount"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Please enter a valid amount"
            return
        }

        val transaction = Transaction(
            title = title,
            amount = amount,
            type = type,
            category = category,
            date = date,
            note = note
        )

        lifecycleScope.launch {
            dao.insertTransaction(transaction)
            runOnUiThread {
                Toast.makeText(this@AddTransactionActivity, "Transaction saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}