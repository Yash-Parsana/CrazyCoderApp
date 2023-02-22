package com.parsanatech.crazycoder.Fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.parsanatech.crazycoder.Adapters.FriendsChatAdapter
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.databinding.ActivityMainBinding
import com.parsanatech.crazycoder.databinding.ActivityPasswordResetBinding
import com.parsanatech.crazycoder.databinding.FragmentChatBinding
import com.parsanatech.crazycoder.models.chatUser
import kotlinx.coroutines.delay


class chatFragment : Fragment() {

    lateinit var binding:FragmentChatBinding
    lateinit var auth:FirebaseAuth
    lateinit var firestoreDb:FirebaseFirestore
    lateinit var friendChatAdapter:FriendsChatAdapter
    val listOfFriends=ArrayList<chatUser>()
    var totalFriends=0
    var fragmentActive=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        Log.d("Inside chatFragment","Yes")
        binding= FragmentChatBinding.inflate(layoutInflater,container,false)

        firestoreDb= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        friendChatAdapter= FriendsChatAdapter()

        binding.recyclerView.adapter=friendChatAdapter
        binding.recyclerView.layoutManager=LinearLayoutManager(context)

        try {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) // if not touchable flag remains in leaderboard then clear it
        }
        catch (e:Exception)
        {
            Log.e("Exception in Locking ui in chat Fragment",e.toString())
        }

        recentFreinds()

        fragmentActive=true
        binding.addbtn.z=10F
        binding.line.z=10F
        binding.chatshimmer.z=10F
        binding.addbtn.setOnClickListener {

            Log.d("Clicked on Add Button","Yes")

            try {
                val dialogbinding=ActivityPasswordResetBinding.inflate(layoutInflater)

                dialogbinding.mainmessage.text="Add Friend\n\nEnter Your Friend's CrazyCoder Username"
                dialogbinding.userinput.hint="UserName"
//            dialogbinding.userinput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)


                val dialog=Dialog(requireContext())
                dialog.setContentView(dialogbinding.root)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialogbinding.submitbtn.setOnClickListener {

                    Log.d("Submit button Clicked","Yes")
                    val userName=dialogbinding.userinput.text.toString().trim()


                    if(userName.isEmpty())
                    {
                        dialogbinding.userinput.setError("Please Enter Your Friend's CrazyCoder Username")
                    }
                    else if(!validate(userName))
                    {
                        dialogbinding.userinput.setError("Wrong Formate A-Z a-z 0-9 _ are allowed")
                    }
                    else{
                        var b=true

                        listOfFriends.forEach {
                            if(it.username==userName)
                                b=false
                        }
                        if(b)
                        {
                            dialogbinding.progressbar.visibility=View.VISIBLE
                            firestoreDb.collection("users").document("userNameToId").get().addOnSuccessListener {it1->
                                if(it1[userName]==null)
                                {
                                    dialogbinding.progressbar.visibility=View.INVISIBLE
                                    dialogbinding.userinput.setError("User With name $userName does not Exist")
                                }
                                else if(it1[userName].toString()==auth.uid.toString())
                                {
                                    dialogbinding.userinput.setError("You can not add your own UserName")
                                    dialogbinding.progressbar.visibility=View.INVISIBLE
                                }
                                else{
                                    firestoreDb.collection("users").document(auth.uid.toString()).get().addOnSuccessListener {

                                        var friendList=it["chatfriends"] as ArrayList<String>


                                        friendList.add(it1[userName].toString())

                                        val list = friendList.distinct()
                                        friendList.clear()
                                        friendList.addAll(list)

                                        firestoreDb.collection("users").document(auth.uid.toString()).update("chatfriends",friendList).addOnSuccessListener {


                                            firestoreDb.collection("users").document(it1[userName].toString()).get().addOnSuccessListener {it3->

                                                val otherFriendList=it3["chatfriends"] as ArrayList<String>

                                                otherFriendList.add(auth.uid.toString())
                                                firestoreDb.collection("users").document(it1[userName].toString()).update("chatfriends",otherFriendList).addOnSuccessListener {

                                                    listOfFriends.clear()
                                                    dialog.dismiss()
                                                }

                                            }.addOnFailureListener {
                                                dialogbinding.progressbar.visibility=View.INVISIBLE
                                                Log.d("Error in Forth Request (Adding Friend)",it.message.toString())
                                            }



                                        }.addOnFailureListener {
                                            dialogbinding.progressbar.visibility=View.INVISIBLE
                                            Log.d("Error in third Request (Adding Friend)",it.message.toString())
                                        }


                                    }.addOnFailureListener {
                                        dialogbinding.progressbar.visibility=View.INVISIBLE
                                        Log.d("Error in second Request (Adding Friend)",it.message.toString())
                                    }
                                }


                            }.addOnFailureListener {
                                dialogbinding.progressbar.visibility=View.INVISIBLE
                                Log.d("Error in first Request (Adding Friend) : ",it.message.toString())
                            }
                        }
                        else{
                            dialog.dismiss()

                        }



                    }


                }

                dialog.show()

            }
            catch (e:Exception)
            {
                Log.e("Exception after clicked on Add button in chatFragment",e.toString())
            }

        }

        return binding.root

    }

    private fun recentFreinds()
    {
        Log.d("In recentFriendsFunction","Yes")
        binding.chatshimmer.visibility=View.VISIBLE
        binding.defaultText.visibility=View.GONE
        if(listOfFriends.isEmpty())
        {
            firestoreDb.collection("users").document(auth.uid.toString()).addSnapshotListener { value, error ->


                if(error!=null)
                {
                    Log.e("Error in Firestore Request in recentFriend Function",error.message.toString())
                    return@addSnapshotListener
                }
                if(value!=null)
                {
                    listOfFriends.clear()
                    val chatFriends=ArrayList<String>()

                    try
                    {
                        val array=value["chatfriends"] as ArrayList<String>
                        chatFriends.addAll(array)

                        val list=chatFriends.distinct()
                        chatFriends.clear()
                        chatFriends.addAll(list)

                        totalFriends=chatFriends.size

                        if(chatFriends.isNotEmpty())
                        {
                            Log.d("ChatFriend List is Not Empty","Yes")
                            RenderUser(chatFriends)
                            binding.defaultText.visibility=View.GONE
                        }
                        else
                        {
                            Log.d("chatFriend List is Empty","Yes")
                            binding.defaultText.visibility=View.VISIBLE
                            binding.chatshimmer.visibility=View.GONE
                        }
                    }
                    catch (e:Exception)
                    {
                        Log.e("Exception in chat fragment",e.toString())
                    }


                }

            }
        }
        else{
            Log.d("Friend List from FireBase is Not Emapty","Yes")
            supplyDataToAdapter()
        }


    }

    private fun RenderUser(chatfriends:ArrayList<String>)
    {
        Log.d("In RenderUser Function chatfriendListSize= ${chatfriends.size}","Yes")

        try {
            Thread{
                Log.d("On Parallel Thread","Yes")
                for (i in 0 until chatfriends.size)
                {
                    Log.d("Making multiple request to firestore","Yes")
                    makerequestToFirestore(chatfriends[i])
                }

                while (totalFriends>0)
                {
                    Log.d("Waithing For Data From FireStore","Yes $totalFriends")
                    if(!fragmentActive)
                    {
                        Log.d("fragment Activity","Yes")
                        listOfFriends.clear()
                        break
                    }
                    Thread.sleep(50)
                }
                if(fragmentActive)
                    supplyDataToAdapter()

            }.start()
        }
        catch (e:Exception)
        {
            Log.e("Exception in parallel thread",e.toString())
        }


    }

    private  fun makerequestToFirestore(userid:String)
    {
        Log.d("In makerequestToFirestore Function with user id : $userid","Yes")
        firestoreDb.collection("users").document(userid).addSnapshotListener { value, error ->

            if(error!=null)
            {
                totalFriends--
                Log.d("FireStore Eror in makerequestToFirestore",error.message.toString())
                return@addSnapshotListener
            }
            if(value!=null)
            {
                val b=value["status"] as Boolean
                Log.d("In makerequestToFirestore Function Request Success","${b} ${value["username"]}  ${listOfFriends.size}")
                var pic:String?=null
                if(value["pic"]!=null)
                {
                    pic=value["pic"].toString()
                }
                var flag=true // flag is used to differentiate snapshot call between data changed and firsttime call
                // if data is changed so there is already exist user in listOfFriends so we simply update value and set falg to false so user won't be inserted in list
                listOfFriends.forEach {
                    if(it.username==value["username"])
                    {

                        flag=false
                        it.status=b
                        it.picture=pic.toString()
                        try {
                            friendChatAdapter.updataData(listOfFriends)
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while calling update data fun of Adapter",e.toString())
                        }
                    }
                }
                Log.d("makerequestToFirestore Flag",flag.toString())
                if(flag)
                {
                    totalFriends--
                    Log.d("makerequestToFirestore user added in listofFriends","Yes $totalFriends")
                    val AdapterUser=chatUser(value["username"].toString(),pic.toString(),b,userid)
                    listOfFriends.add(AdapterUser)
                }

            }

        }
    }

    private fun supplyDataToAdapter()
    {

        Log.d("Supplying Data To Adapter listSize : ${listOfFriends.size} ${requireActivity()} ","Yes")

        try
        {
            requireActivity().runOnUiThread(Runnable {
                binding.chatshimmer.visibility = View.GONE
                try {
                    friendChatAdapter.updataData(listOfFriends)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while calling updateData fun of Adapter",e.toString())
                }
            })
        }
        catch (e:Exception)
        {
            Log.e("Exception supplyDataAdapter on requireActivity",e.toString())
        }
    }


    fun validate(userEnterdName:String):Boolean{

        for(i in 0..userEnterdName.length-1)
        {
            if(!((userEnterdName[i]>='a'&&userEnterdName[i]<='z')||(userEnterdName[i]>='A'&&userEnterdName[i]<='Z')||(userEnterdName[i]>='0'&&userEnterdName[i]<='9')||userEnterdName[i]=='_'))
            {
                return false
            }
        }
        return true
    }

    override fun onStop() {
        super.onStop()

        Log.d("OnStop()","Yes")
        fragmentActive=false
    }



}

