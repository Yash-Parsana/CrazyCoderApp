package com.parsanatech.crazycoder

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaParser
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.parsanatech.crazycoder.Adapters.LetUsChatAdapter
import com.parsanatech.crazycoder.databinding.ActivityLetUsChatBinding
import com.parsanatech.crazycoder.models.messageModel
import java.text.SimpleDateFormat
import java.util.*


class LetUsChatActivity : AppCompatActivity() {

    lateinit var binding:ActivityLetUsChatBinding
    lateinit var auth:FirebaseAuth
    lateinit var firestoreDb:FirebaseFirestore
    lateinit var calender:Calendar
    lateinit var letUsChatAdapter:LetUsChatAdapter
    lateinit var senderChat:String
    lateinit var receiverChat:String
    lateinit var database:FirebaseDatabase
    var backButtonPressed=false
    var allmessageLoaded=false

    val messagelist=ArrayList<messageModel>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLetUsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth= FirebaseAuth.getInstance()
        firestoreDb=FirebaseFirestore.getInstance()
        calender= Calendar.getInstance()
        database= FirebaseDatabase.getInstance()

        val senderId=auth.uid.toString()
        val receiverId=intent.getStringExtra("userId").toString()
        val pic=intent.getStringExtra("profilePic").toString()
        val username=intent.getStringExtra("userName").toString()

        senderChat=senderId+receiverId
        receiverChat=receiverId+senderId

        try {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        catch (e:Exception)
        {
            Log.e("Exception while unlocking Ui",e.toString())
        }


        letUsChatAdapter=LetUsChatAdapter(this,senderId,receiverId)
        binding.letuschatRecyclerView.adapter=letUsChatAdapter
        binding.letuschatRecyclerView.layoutManager=LinearLayoutManager(this)

        binding.shimmer.z=10F


        GlidProcess(pic)


        binding.backButton.setOnClickListener {
            backButtonPressed=true
            Log.d("Clicked on backButton","Yes")
            this.finish()
        }


        binding.username.text=username


        firestoreDb.collection("users").document(receiverId).addSnapshotListener{value,error->

            if(error!=null)
            {
                Log.e("Error in Firestore Request whie checking status",error.message.toString())
                return@addSnapshotListener
            }
            if(value!=null)
            {
                try {
                    val status:Boolean=value["status"] as Boolean

                    if(status){
                        binding.status.setTextColor(Color.parseColor("#00FF00"))
                        binding.status.text="Online"
                    }
                    else{
                        binding.status.setTextColor(Color.parseColor("#FF03DAC5"))
                        binding.status.text="Offline"
                    }

                }
                catch (e:Exception)
                {
                    Log.e("Exception while status checking (value!=null)",e.toString())
                }
            }

        }


        firestoreDb.collection("chat").document(senderChat).collection(senderChat).orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Log.w("TAG", "Listen failed.", error)
                return@addSnapshotListener
            }

            if(value!=null)
            {
                binding.letuschatRecyclerView.smoothScrollToPosition(letUsChatAdapter.getItemCount());

                Log.d("Read","Yes")
                messagelist.clear()
                var lastDate:String=""
                val n=value.documents.size
                value.documents.forEach {
                    Log.d("Timestamp",it["time"].toString())
                    try{
                        val timestamp=it["time"].toString().toLong()
                        val showdate=SimpleDateFormat("dd/MM/yyyy").format(timestamp)



                        if(lastDate!=showdate)
                        {
                            messagelist.add(messageModel("","Date",showdate,"",""))
                        }
                        lastDate=showdate

                        val timeFormate=SimpleDateFormat("hh:mm a")
                        val showtime=timeFormate.format(timestamp).toString()
                        val currMessage=messageModel(it.id,it["senderId"].toString(),it["message"].toString(),showtime,it.id)
                        messagelist.add(currMessage)
                    }
                    catch (e:Exception)
                    {
                        Log.e("Exception whie Formating Date",e.toString())
                    }

                }


                try {
                    binding.toolbar.visibility=View.VISIBLE
                    binding.linear.visibility=View.VISIBLE
                    binding.shimmer.visibility=View.GONE
                    letUsChatAdapter.updateData(messagelist)
                    allmessageLoaded=true
                }
                catch (e:Exception)
                {
                    Log.e("Exception while calling updateData of Adapter",e.toString())
                }


            }


        }

        binding.sendbtn.setOnClickListener {

            if(!binding.message.text.isEmpty())
            {
                val message=binding.message.text.toString().trim()
                binding.message.setText("")


                firestoreDb.collection("chat").document(senderChat).collection(senderChat).add(
                    hashMapOf(
                        "time" to Date().time,
                        "senderId" to senderId,
                        "message" to message
                    )).addOnSuccessListener {

                        val id=it.id
                       firestoreDb.collection("chat").document(receiverChat).collection(receiverChat).document(id).set(
                           hashMapOf(
                               "time" to Date().time,
                               "senderId" to senderId,
                               "message" to message
                           )).addOnSuccessListener {


                       }.addOnFailureListener {
                           Log.d("Faild to store data at receiver Side",it.message.toString())
                       }
                }.addOnFailureListener {
                    Log.d("Failed to store data at sender Side",it.message.toString())
                }

            }

        }




    }

    @SuppressLint("ResourceType")
    private fun GlidProcess(pic:String)
    {
        if(pic!=null)
        {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.ic_baseline_account_circle_24)
            Glide.with(this).setDefaultRequestOptions(requestOption).load(pic).into(binding.profileimage)
        }
        else
        {
            binding.profileimage.setCircleBackgroundColorResource(R.drawable.ic_baseline_account_circle_24)
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backButtonPressed=true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onStart() {
        super.onStart()
        Log.d("LetUsChat Activity Starting","Yes")
        firestoreDb.collection("users").document(auth.uid.toString()).update("status",true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("LetUsChat Activity Stoping","Yes")
        if(!backButtonPressed)
            firestoreDb.collection("users").document(auth.uid.toString()).update("status",false)
    }




}