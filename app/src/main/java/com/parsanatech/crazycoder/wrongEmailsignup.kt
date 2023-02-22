package com.parsanatech.crazycoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.parsanatech.crazycoder.databinding.ActivityWrongEmailsignupBinding

class wrongEmailsignup : AppCompatActivity() {

    lateinit var binding:ActivityWrongEmailsignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityWrongEmailsignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.closebtn.setOnClickListener {
            finish()
        }




    }
}