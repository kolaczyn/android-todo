package com.example.todos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.todos.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOne.setOnClickListener {
            replaceFragment(FragmentOne())
        }


        val apiHelper = ApiHelperImpl(RetrofitBuilder.apiService)


        MainScope().launch {
            binding.buttonTwo.setOnClickListener {
                replaceFragment(FragmentTwo())
            }


            apiHelper.createTodos().collect() { todos ->
                binding.buttonOne.text = todos.size.toString()

            }

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment)
        fragmentTransaction.commit()
    }
}