package com.example.zipextectorapp.Adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.zipextectorapp.R


class CustomSpinnerAdapter(context: Context, items: Array<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray_color))
        textView.setPadding(20, 20, 20, 20)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray_color))
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        textView.setPadding(20, 20, 20, 20) // Add padding to the TextView
        return view
    }
}






