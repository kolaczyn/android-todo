package com.example.todos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todos.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiHelper = ApiHelperImpl(RetrofitBuilder.apiService)

        var items = mutableListOf<TodoDto>();
        val adapter = ListAdapter(items)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnClickListener { model ->
            lifecycleScope.launch {
                apiHelper.deleteTodo(model.id).collect {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.deleted_todo, model.id.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        adapter.setToggleClickListener {
            lifecycleScope.launch {
                apiHelper.toggleDone(it.id, !it.done).collect {
                    if (it == null) {
                        notifySomethingWentWrong()
                        return@collect
                    }
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toggled_todo, it.id.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.buttonOne.setOnClickListener {
            lifecycleScope.launch {
                val text = binding.editText.text.toString()
                if (text.isBlank()) {
                    return@launch
                }
                apiHelper.createTodos(text).collect {
                    if (it == null) {
                        Log.i("MainActivity", "Error creating todo")
                        return@collect
                    }
                    binding.editText.text.clear()
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.created_todo, it.text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.buttonTwo.setOnClickListener {
            lifecycleScope.launch {
                val text = binding.editText.text.toString().toIntOrNull() ?: return@launch
                apiHelper.deleteTodo(text).collect() {
                    if (it == null) {
                        notifySomethingWentWrong()
                        return@collect
                    }
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.first_todo_id, it.id.toString()),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        lifecycleScope.launch {
            apiHelper.getTodos().collect {
                if (it == null) {
                    notifySomethingWentWrong()
                    return@collect
                }
                binding.totalTodos.text = getString(R.string.total_todos, it.size.toString())
            }
        }

        lifecycleScope.launch {
            apiHelper.getTodos().collect() {
                if (it == null) {
                    notifySomethingWentWrong()
                    return@collect
                }
                items.clear()
                items.addAll(it)
                adapter.notifyDataSetChanged()
            }

        }

    }

    private fun notifySomethingWentWrong() {
        Toast.makeText(
            this,
            "Something went wrong!",
            Toast.LENGTH_SHORT
        ).show()
    }

}