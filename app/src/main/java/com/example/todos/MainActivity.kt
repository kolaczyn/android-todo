package com.example.todos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todos.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiHelper = ApiHelperImpl(RetrofitBuilder.apiService)

        var items = mutableListOf<TodoDto>();
        val adapter = ListAdapter(items)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        binding.buttonOne.setOnClickListener {
            lifecycleScope.launch {
                val text = binding.editText.text.toString()
                if (text.isBlank()) {
                    return@launch
                }
                apiHelper.createTodos(text).collect {
                    binding.editText.text.clear()
                    Toast.makeText(
                        this@MainActivity,
                        "Created todo: ${it.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        lifecycleScope.launch {
            apiHelper.getTodos().collect() { todos ->
                binding.totalTodos.text = "Total todos: ${todos.size}"
            }
        }

        lifecycleScope.launch {
            apiHelper.getTodos().collect() { todos ->
                items.clear()
                val newItems = todos.reversed().toMutableList()
                items.addAll(newItems)
                adapter.notifyDataSetChanged()
            }

        }
    }
}