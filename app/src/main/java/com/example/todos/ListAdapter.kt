package com.example.todos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private val items: MutableList<TodoDto>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.listItemTextView)
        val deleteButton: Button = itemView.findViewById(R.id.itemDeleteButton)
    }

    private var listener: ((position: Int, model: TodoDto) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.deleteButton.setOnClickListener {
            listener?.invoke(position, item)
        }
        holder.textView.text =
            "${item.id}: ${item.text} is ${if (item.done) "done" else "not done"}"
    }

    fun setOnClickListener(listener: (position: Int, model: TodoDto) -> Unit) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
