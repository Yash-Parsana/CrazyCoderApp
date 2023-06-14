package com.parsanatech.crazycoder

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    private val appTitle = "Crazy Coder"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val textView = findViewById<TextView>(R.id.nameTextView)
//        val imageView = findViewById<ImageView>(R.id.logo)

//        animateImage(imageView)
        animateText(textView, appTitle)

        val interval:Long=4250

        object : CountDownTimer(interval, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                startActivity(Intent(this@SplashScreenActivity, SignUp::class.java))
                finish()
            }
        }.start()
    }

//    private fun animateImage(imageView: ImageView) {
//        val animation = AlphaAnimation(0f, 1f)
//        animation.duration = 2000
//        animation.fillAfter = true
//
//        imageView.startAnimation(animation)
//    }
    private fun animateText(textView: TextView, text: String) {
        val animationDuration = 2000L // 1 second
        val delayBetweenLetters = 180L // 0.2 second

        for (i in text.indices) {
            val handler = Handler()
            handler.postDelayed({
                textView.text = text.substring(0, i + 1)
            }, (i * delayBetweenLetters) + animationDuration)
        }

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, (text.length * delayBetweenLetters) + animationDuration + 1000L) // Add extra delay after animation completes
    }


}