package com.parsanatech.crazycoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText

class AddPlatformActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_platform)


        supportActionBar?.hide()

        val list=findViewById<EditText>(R.id.autoCompleteTextView)
        list.inputType= InputType.TYPE_NULL

        // get reference to the string array that we just created
        val plaforms = resources.getStringArray(R.array.platforms)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, plaforms)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)



    }
}