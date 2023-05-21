package com.parsanatech.crazycoder.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.authentication.SignUp

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val interval:Long=2000

        object : CountDownTimer(interval, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(this@SplashScreenActivity, SignUp::class.java))
                finish()
            }
        }.start()


    }
}