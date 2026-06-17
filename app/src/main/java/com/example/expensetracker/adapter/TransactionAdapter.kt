package com.example.expensetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.database.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onItemLongClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivTransactionIcon)

        fun bind(transaction: Transaction) {
            tvTitle.text = transaction.title
            tvCategory.text = transaction.category
            tvDate.text = transaction.date

            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            val formattedAmount = formatter.format(transaction.amount)

            if (transaction.type == "income") {
                tvAmount.text = "+$formattedAmount"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.income_green))
                ivIcon.setImageResource(R.drawable.ic_income)
                ivIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.income_green))
            } else {
                tvAmount.text = "-$formattedAmount"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.expense_red))
                ivIcon.setImageResource(R.drawable.ic_expense)
                ivIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.expense_red))
            }

            itemView.setOnLongClickListener {
                onItemLongClick(transaction)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem == newItem
    }
}