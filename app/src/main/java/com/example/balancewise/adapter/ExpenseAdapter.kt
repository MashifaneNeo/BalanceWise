package com.example.balancewise.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.balancewise.R
import com.example.balancewise.database.Expense

class ExpenseAdapter(
    private var items: List<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvItemAmount)
        val tvDescription: TextView = view.findViewById(R.id.tvItemDescription)
        val tvDate: TextView = view.findViewById(R.id.tvItemDate)
        val imgReceipt: ImageView = view.findViewById(R.id.imgItemReceipt)
        val tvCategory: TextView = view.findViewById(R.id.tvItemCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = items[position]
        holder.tvAmount.text = "R %.2f".format(item.amount)
        holder.tvDescription.text = item.description
        holder.tvDate.text = item.date
        holder.tvCategory.text = "Category: ${item.categoryId}"

        if (!item.photoUri.isNullOrEmpty()) {
            holder.imgReceipt.visibility = View.VISIBLE
            holder.imgReceipt.setImageURI(Uri.parse(item.photoUri))
        } else {
            holder.imgReceipt.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: List<Expense>) {
        items = newList
        notifyDataSetChanged()
    }
}