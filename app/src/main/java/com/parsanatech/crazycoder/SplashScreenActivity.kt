package com.parsanatech.crazycoder

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val sharedpref: SharedPreferences =
            applicationContext.getSharedPreferences(
                "com.itssuryansh.taaveez",
                MODE_PRIVATE
            )

        val interval:Long=2000

        object : CountDownTimer(interval, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
               // startActivity(Intent(this@SplashScreenActivity, SignUp::class.java))
                finish()
            }
        }.start()

        val token: String? = sharedpref.getString("token", null)
        if (token == "false" || token == null) {
            val mv = Intent(this, MyCustomAppIntro::class.java)
            startActivity(mv)

                  sharedpref.edit().putString("token", "true").apply()
        } else {
            sharedpref.edit().putString("token", "false").apply()
            startActivity(Intent(this@SplashScreenActivity, MyCustomAppIntro::class.java))

        }


    }
}