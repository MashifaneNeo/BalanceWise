package com.example.balancewise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.balancewise.R
import com.example.balancewise.model.CategoryTotalItem

class CategoryTotalAdapter(private val items: List<CategoryTotalItem>) : RecyclerView.Adapter<CategoryTotalAdapter.TotalViewHolder>() {

    class TotalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvTotalCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvTotalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_total, parent, false)
        return TotalViewHolder(view)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val item = items[position]
        holder.tvCategory.text = item.category
        holder.tvAmount.text = "R %.2f".format(item.total)
    }

    override fun getItemCount(): Int = items.size
}

