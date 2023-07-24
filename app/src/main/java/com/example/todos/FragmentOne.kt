package com.example.todos

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentOne : Fragment(R.layout.fragment_one) {

    companion object {
        private const val ARG_MESSAGE = "arg_message"

        fun newInstance(message: String): FragmentOne {
            val args = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
            val fragment = FragmentOne()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the message from the arguments and display it in the TextView
        val message = arguments?.getString(ARG_MESSAGE) ?: "Default Message"
        val textView: TextView = view.findViewById(R.id.textViewMessage)
        textView.text = message
    }
}
