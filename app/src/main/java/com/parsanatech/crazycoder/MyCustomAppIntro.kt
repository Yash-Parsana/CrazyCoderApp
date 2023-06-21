package com.parsanatech.crazycoder

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment

class MyCustomAppIntro : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        addSlide(AppIntroFragment.createInstance(
            title = "Keep track of all your competitions!",
            description = "See all major DSA and competitive programming competitions happening across all major platforms in one go!",
            imageDrawable = R.drawable.is1,
            titleColorRes = R.color.white,
            descriptionColorRes = R.color.white,
            backgroundColorRes = R.color.black,
            titleTypefaceFontRes = R.font.quicksand_light,
            descriptionTypefaceFontRes = R.font.quicksand_light,
        ))
        addSlide(AppIntroFragment.createInstance(
            title = "Get daily updates about new hackathons and competitions",
            description = "Get notified about latest competitions immediately, so that u never miss your contests!",
            imageDrawable = R.drawable.is2,
            titleColorRes = R.color.white,
            descriptionColorRes = R.color.white,
            backgroundColorRes = R.color.black,
            titleTypefaceFontRes = R.font.quicksand_light,
            descriptionTypefaceFontRes = R.font.quicksand_light,
        ))
        addSlide(AppIntroFragment.createInstance(
            title = "Your journey to 5 stars",
            description = "Be on top of the leaderboards and hail acclaims by adding a helpful hand to your productivity! ",
            imageDrawable = R.drawable.is3,
            titleColorRes = R.color.white,
            descriptionColorRes = R.color.white,
            backgroundColorRes = R.color.black,
            titleTypefaceFontRes = R.font.quicksand_light,
            descriptionTypefaceFontRes = R.font.quicksand_light,
        ))
        addSlide(AppIntroFragment.createInstance(
            title = "Win the Race!",
            description = "Stay on top of your competitive programming journey!",
            imageDrawable = R.drawable.logo,
            titleColorRes = R.color.white,
            descriptionColorRes = R.color.white,
            backgroundColorRes = R.color.black,
            titleTypefaceFontRes = R.font.quicksand_light,
            descriptionTypefaceFontRes = R.font.quicksand_light,

        ))

        isSkipButtonEnabled = false


    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)


    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        val changepage = Intent(this, MainActivity::class.java)
        startActivity(changepage)

    }
}