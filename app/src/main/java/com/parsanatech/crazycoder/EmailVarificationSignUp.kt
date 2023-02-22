package com.parsanatech.crazycoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.parsanatech.crazycoder.databinding.ActivityEmailVarificationSignUpBinding

class EmailVarificationSignUp : AppCompatActivity() {

    lateinit var binding:ActivityEmailVarificationSignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityEmailVarificationSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closebtn.setOnClickListener {
            finish()
        }



    }



}