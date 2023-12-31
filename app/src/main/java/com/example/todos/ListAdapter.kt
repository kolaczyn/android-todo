package com.example.todos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


typealias Listener = (model: TodoDto) -> Unit

class ListAdapter(private val items: MutableList<TodoDto>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.listItemTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.itemDeleteButton)
        val checkBox: CheckBox = itemView.findViewById(R.id.listItemCheckBox)
    }

    private var listener: Listener? = null
    private var toggleListener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.deleteButton.setOnClickListener {
            listener?.invoke(item)
        }
        holder.checkBox.isChecked = item.done
        holder.checkBox.setOnCheckedChangeListener { _, _ ->
            toggleListener?.invoke(item)
        }
        holder.textView.text = item.text
    }

    fun setOnClickListener(listener: Listener) {
        this.listener = listener
    }

    fun setToggleClickListener(listener: Listener) {
        this.toggleListener = listener
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
