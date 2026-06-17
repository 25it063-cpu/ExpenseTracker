package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.adapter.TransactionAdapter
import com.example.expensetracker.database.ExpenseDatabase
import com.example.expensetracker.database.Transaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var tvBalance: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var fab: FloatingActionButton

    private val db by lazy { ExpenseDatabase.getDatabase(this) }
    private val dao by lazy { db.transactionDao() }
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        observeData()

        fab.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        tvBalance = findViewById(R.id.tvBalance)
        tvIncome = findViewById(R.id.tvTotalIncome)
        tvExpense = findViewById(R.id.tvTotalExpense)
        tvEmpty = findViewById(R.id.tvEmpty)
        fab = findViewById(R.id.fab)
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            showDeleteDialog(transaction)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        dao.getAllTransactions().observe(this) { transactions ->
            adapter.submitList(transactions)
            tvEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (transactions.isEmpty()) View.GONE else View.VISIBLE
        }

        dao.getTotalIncome().observe(this) { income ->
            val totalIncome = income ?: 0.0
            tvIncome.text = formatter.format(totalIncome)
            updateBalance()
        }

        dao.getTotalExpense().observe(this) { expense ->
            val totalExpense = expense ?: 0.0
            tvExpense.text = formatter.format(totalExpense)
            updateBalance()
        }
    }

    private var currentIncome = 0.0
    private var currentExpense = 0.0

    private fun updateBalance() {
        dao.getTotalIncome().observe(this) { income ->
            currentIncome = income ?: 0.0
            dao.getTotalExpense().observe(this) { expense ->
                currentExpense = expense ?: 0.0
                val balance = currentIncome - currentExpense
                tvBalance.text = formatter.format(balance)
            }
        }
    }

    private fun showDeleteDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete \"${transaction.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    dao.deleteTransaction(transaction)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}