package com.parsanatech.crazycoder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.parsanatech.crazycoder.Fragment.*
import com.parsanatech.crazycoder.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import io.grpc.internal.LogExceptionRunnable

class MainActivity : AppCompatActivity() {

    private val MODE_PRIVATE: Int=0
    lateinit var binding:ActivityMainBinding
    lateinit var auth:FirebaseAuth
    lateinit var fireStoreDb:FirebaseFirestore
    lateinit var updateManager:AppUpdateManager
    private val MY_REQUEST_CODE = 500
    lateinit var reviewManager:ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        reviewManager=ReviewManagerFactory.create(this)
        updateManager=AppUpdateManagerFactory.create(this)
        updateManager.appUpdateInfo.addOnSuccessListener {

            if(it.updateAvailability()==UpdateAvailability.UPDATE_AVAILABLE&&it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
            {
                try {
                    updateManager.startUpdateFlowForResult(it,AppUpdateType.FLEXIBLE,this,MY_REQUEST_CODE)
                }
                catch (e:Exception)
                {

                }
            }

        }.addOnFailureListener {
            Log.e("Failed to load update avaiable Dialog",it.toString())
        }



        try
        {
            val sharedPref = this.getSharedPreferences(
                "com.example.CrazyCoder", this.MODE_PRIVATE)?:return

            with(sharedPref.edit())
            {
                putBoolean("Goinning_in_LetUsChat_Activity",false)
                apply()
            }
        }
        catch(e:Exception)
        {
            Log.e("Exception in sharedPreference MainActivity",e.toString())
        }



        fireStoreDb= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        binding.bottomnavigationbar.setItemSelected(R.id.contests)

        val homefragment=contestsFragment()
        val leaderfragment=leaderBoardFragment()
        val chatfragment=chatFragment()
        val profilefragment=profileFragment()
        val sdefragment=sdeFragment()


        setCurrentFragment(homefragment)

        binding.bottomnavigationbar.setOnItemSelectedListener {

            when(it)
            {
                R.id.contests->setCurrentFragment(homefragment)
                R.id.leadrboard->setCurrentFragment(leaderfragment)
                R.id.sde->setCurrentFragment(sdefragment)
                R.id.chat->setCurrentFragment(chatfragment)
                R.id.profile->setCurrentFragment(profilefragment)
            }
        }


    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    override fun onStart() {
        super.onStart()
        Log.d("Main Activity Starting","Yes")
        fireStoreDb.collection("users").document(auth.uid.toString()).update("status",true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("Main Activity Stoping","Yes")

        try {
            val sharedPref = this.getSharedPreferences(
                "com.example.CrazyCoder", this.MODE_PRIVATE)?:return
            val flag=sharedPref.getBoolean("Goinning_in_LetUsChat_Activity",false)
            Log.d("Goinning_in_LetUsChat_Activity",flag.toString())

            if(!flag)
            {
                fireStoreDb.collection("users").document(auth.uid.toString()).update("status",false)
            }
            else{
                with(sharedPref.edit())
                {
                    putBoolean("Goinning_in_LetUsChat_Activity",false)
                    apply()
                }
            }
        }
        catch (e:Exception)
        {
            Log.e("Exception in sharedpreference MainActivity In on stop",e.toString())
        }


    }

    override fun onResume() {
        super.onResume()
        inProgressUpdate()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        try {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result

                    val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {

                    }


                } else {
                    Log.e("Exception in loading review box",task.exception.toString())
                }
            }
        }
        catch (e:Exception)
        {
            Log.e("Exception while tacking review",e.toString())
        }

    }

    private fun inProgressUpdate()
    {
        updateManager.appUpdateInfo.addOnSuccessListener {
            if(it.updateAvailability()==UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            {

                try {
                    updateManager.startUpdateFlowForResult(it,AppUpdateType.FLEXIBLE,this,MY_REQUEST_CODE)

                }
                catch (e:Exception)
                {
                    Log.e("inprogressUpdate process failed",e.toString())
                }
            }
        }
    }

}


