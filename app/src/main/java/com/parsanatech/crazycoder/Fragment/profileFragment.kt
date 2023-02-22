package com.parsanatech.crazycoder.Fragment

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.parsanatech.crazycoder.*
import com.parsanatech.crazycoder.Adapters.leaderBoardItemSelected
import com.parsanatech.crazycoder.Adapters.platformAdapterLeaderBoard
import com.parsanatech.crazycoder.Adapters.profile_userInfo_Adapter
import com.parsanatech.crazycoder.databinding.ActivityAddPlatformBinding
import com.parsanatech.crazycoder.databinding.FragmentProfileBinding
import com.parsanatech.crazycoder.models.DbViewModel
import com.parsanatech.crazycoder.models.profile_userinfo
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class profileFragment : Fragment(), leaderBoardItemSelected {

    val MODE_PRIVATE:Int=0
    var userName=""
    var currSelectedPlatform=0
    lateinit var binding:FragmentProfileBinding
    lateinit var cloudStorage: FirebaseStorage
    lateinit var auth:FirebaseAuth
    lateinit var fireStore:FirebaseFirestore
    lateinit var platFormAdapter:platformAdapterLeaderBoard
    lateinit var infoAdapter:profile_userInfo_Adapter
    lateinit var viewmodel:DbViewModel
    val platformListforApi = ArrayList<String>()
    val propertyListCf=ArrayList<profile_userinfo>()
    val propertyListCc=ArrayList<profile_userinfo>()
    val propertyListLc=ArrayList<profile_userinfo>()
    val propertyListSj=ArrayList<profile_userinfo>()
    var userAdded=false
    var sameplatForm=false
    var lastAddedplatform=-1
    var finalImage:ByteArray?=null
    var codeforcesUrl:String=""
    var codechefUrl:String=""
    var leetCodeUrl:String=""
    var spojUrl:String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        binding= FragmentProfileBinding.inflate(layoutInflater,container,false)
        cloudStorage= FirebaseStorage.getInstance()
        auth= FirebaseAuth.getInstance()
        fireStore= FirebaseFirestore.getInstance()
        viewmodel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance((activity?.applicationContext as Application))).get(
            DbViewModel::class.java)

        platFormAdapter= platformAdapterLeaderBoard(this,2)
        infoAdapter= profile_userInfo_Adapter()



        try {
            val sharedPref = requireActivity().getSharedPreferences(
                "com.example.CrazyCoder", this.MODE_PRIVATE)
            userName=sharedPref.getString("userName","CrazyCoder").toString()

        }
        catch (e:Exception)
        {
            Log.e("Exception while reading shared preference",e.toString())
        }

        binding.platformrecyclerView.adapter=platFormAdapter
        binding.platformrecyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        binding.userInfoRecyclerviwInprofile.adapter=infoAdapter
        binding.userInfoRecyclerviwInprofile.layoutManager=LinearLayoutManager(context)

        platformListforApi.add("codeforces")
        platformListforApi.add("leetcode")
        platformListforApi.add("spoj")
        platformListforApi.add("codechef")

        try {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) // if not touchable flag remains in leaderboard then clear it
        }
        catch (e:Exception)
        {
            Log.e("Exception while unlocking Ui",e.toString())
        }

        AssignValue()
        leaderBoardItemSelected("codechef")


        binding.swipRefreshView.setOnRefreshListener {

            if(currSelectedPlatform==0)
            {
                propertyListCf.clear()
                leaderBoardItemSelected("codeforces")
            }
            else if(currSelectedPlatform==1)
            {
                propertyListLc.clear()
                leaderBoardItemSelected("leetcode")
            }
            else if(currSelectedPlatform==2)
            {
                propertyListSj.clear()
                leaderBoardItemSelected("spoj")
            }
            else if(currSelectedPlatform==3)
            {
                propertyListSj.clear()
                leaderBoardItemSelected("codechef")
            }
            binding.swipRefreshView.isRefreshing=false

        }

        try {
            var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if(data!!.data!=null)
                    {
                        binding.progressbar.visibility=View.VISIBLE
                        val filepath: Uri = data.data!!

                        if(Build.VERSION.SDK_INT<29)
                        {
                            Log.d("BuilderVersion",Build.VERSION.SDK_INT.toString())
                            val bitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,filepath)
                            val boss=ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG,25,boss)
                            finalImage=boss.toByteArray()
                            Log.d("FinalImage In if <29",finalImage.toString())

                        }
                        else{
                            Log.d("BuilderVersion",Build.VERSION.SDK_INT.toString())
                            val bitmap=ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver,filepath))
                            val boss=ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG,20,boss)
                            finalImage=boss.toByteArray()
                            Log.d("FinalImage In If >29",finalImage.toString())

                        }

                        val reference:StorageReference=cloudStorage.reference.child("images").child(auth.uid!!)
                        Log.d("FinalImage",finalImage.toString())
                        if(finalImage!=null)
                        {
                            reference.putBytes(finalImage!!).addOnSuccessListener {

                                reference.downloadUrl.addOnSuccessListener {it1->

                                    Log.d("URI",it.toString())
                                    val requestOption= RequestOptions()
                                    requestOption.placeholder(R.drawable.ic_baseline_account_circle_24_green)

                                    try {
                                        Glide.with(requireActivity()).setDefaultRequestOptions(requestOption).load(it1.toString()).into(binding.profileImage)
                                    }
                                    catch (e:Exception)
                                    {
                                        Log.e("Exception in glide process",e.toString())
                                    }

                                    fireStore.collection("users").document(auth.uid.toString()).update("pic",it1.toString()).addOnSuccessListener {
                                        binding.progressbar.visibility=View.INVISIBLE
                                    }.addOnFailureListener {
                                        Log.e("Exception while storing picture on firestore",it.toString())
                                    }

                                }.addOnFailureListener {
                                    Log.e("Exception in downloadUrl",it.toString())
                                }

                            }.addOnFailureListener {
                                Log.d("Faild To store Image ByteArray",it.message.toString())
                            }
                        }

                    }
                }
            }

            binding.profileImage.setOnClickListener {
                val intent = Intent()
                intent.setAction(Intent.ACTION_GET_CONTENT)
                intent.type="image/*"
                resultLauncher.launch(intent)
            }
        }
        catch (e:Exception)
        {
            Log.e("Exception while taking image from mobile storage",e.toString())
        }

        binding.imageCf.setOnClickListener {

            if(codeforcesUrl!="")
            {
                val intent=Intent(Intent.ACTION_VIEW, Uri.parse(codeforcesUrl))
                startActivity(intent)
            }
            else
            {
                Toast.makeText(requireContext(),"Please Add Your Codeforces Handle",Toast.LENGTH_LONG).show()
            }

        }
        binding.imageLc.setOnClickListener {

            if(leetCodeUrl!="")
            {
                val intent=Intent(Intent.ACTION_VIEW, Uri.parse(leetCodeUrl))
                startActivity(intent)
            }
            else{
                Toast.makeText(requireContext(),"Please Add Your Leetcode Handle",Toast.LENGTH_LONG).show()
            }

        }
        binding.imageSj.setOnClickListener {
            if(spojUrl!="")
            {
                val intent=Intent(Intent.ACTION_VIEW,Uri.parse(spojUrl))
                startActivity(intent)
            }
            else{
                Toast.makeText(requireContext(),"Please Add Your Spoj Handle",Toast.LENGTH_LONG).show()
            }
        }

        binding.AddPlatform.setOnClickListener {

            try {
                val addPlatformbinding = ActivityAddPlatformBinding.inflate(layoutInflater)

                addPlatformbinding.autoCompleteTextView.inputType = InputType.TYPE_NULL

                // get reference to the string array that we just created
                val plaforms = resources.getStringArray(R.array.platforms)
                // create an array adapter and pass the required parameter
                // in our case pass the context, drop down layout , and array.
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items_profile, plaforms)
                // get reference to the autocomplete text view
                // set adapter to the autocomplete tv to the arrayAdapter
                addPlatformbinding.autoCompleteTextView.setAdapter(arrayAdapter)

                val dialog = Dialog(requireContext())
                dialog.setContentView(addPlatformbinding.root)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//            val selectedValue: String = (addFriendbinding.autoCompleteTextView as AutoCompleteTextView).text.toString()


                var selectedPlatform: String = ""

                try {
                    addPlatformbinding.autoCompleteTextView.onItemClickListener =
                        AdapterView.OnItemClickListener { adapterView, view, position, id ->
                            selectedPlatform = platformListforApi.get(position)
                        }
                }
                catch(e:Exception)
                {
                    Log.e("Exception while selecting dropdown list",e.toString())
                }

                addPlatformbinding.button.setOnClickListener {

                    addPlatformbinding.progressbar.visibility = View.VISIBLE

                    val userHandle = addPlatformbinding.userhandle.text.toString().trim()

                    if (selectedPlatform.isEmpty()) {
                        addPlatformbinding.autoCompleteTextView.setError("Please select a Platform")
                        addPlatformbinding.progressbar.visibility = View.INVISIBLE
                    }
                    if (userHandle.isEmpty()) {
                        addPlatformbinding.userhandle.setError("Please Enter Handle")
                        addPlatformbinding.progressbar.visibility = View.INVISIBLE
                    } else {

                        var finalUrl:String
                        if(selectedPlatform=="codeforces")
                        {
                            finalUrl="https://codeforces.com/api/user.info?handles="+userHandle
                        }
                        else if(selectedPlatform=="leetcode")
                        {
                            finalUrl="https://leetcode-stats-api.herokuapp.com/"+userHandle
                        }
                        else
                        {
                            finalUrl = "https://competitive-coding-api.herokuapp.com/api/"+ selectedPlatform + "/" + userHandle
                        }

                        val queue = Volley.newRequestQueue(context)

                        val jsonObject =
                            JsonObjectRequest(Request.Method.GET, finalUrl, null, { response ->

                                Log.d("Responsse", response.toString())

                                if (response.has("status")&&response.getString("status").lowercase() == "success"||response.getString("status") == "OK") {

                                    Log.d("Status success or ok","Yse")
                                    userAdded=true
                                    addPlatformbinding.progressbar.visibility = View.INVISIBLE
                                    if (selectedPlatform == "codeforces") {
                                        lastAddedplatform=0
                                        if(currSelectedPlatform==0)sameplatForm=true
                                        codeforcesUrl="https://codeforces.com/profile/"+userHandle
                                        binding.shimmer.visibility=View.VISIBLE
                                        Log.d("codeforces : ","userAdded : $userAdded sameplatform $sameplatForm")

                                        var MyHandle:String=""
                                        try{
                                            MyHandle=response.getJSONArray("result").getJSONObject(0).getString("handle")
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.d("Exception in codeforces user handle",e.message.toString())
                                        }


                                        try {
                                            viewmodel.insertMyPlatform(MyPlatforms("codeforces",MyHandle))
                                            viewmodel.FriendInsertCodeforces(FriendCodeforces(handle = MyHandle, itsMe = true))
                                            Log.d("Inserted","Yes")
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception in storing data at local database",e.toString())
                                        }

                                    }
                                    else if (selectedPlatform == "leetcode") {
                                        lastAddedplatform=1
                                        if(currSelectedPlatform==1)sameplatForm=true
                                        leetCodeUrl="https://leetcode.com/"+userHandle
                                        binding.shimmer.visibility=View.VISIBLE
//                                        binding.progressbar.visibility=View.VISIBLE
                                        try
                                        {
                                            viewmodel.insertMyPlatform(MyPlatforms("leetcode", userHandle))
                                            viewmodel.FriendInsertLeetcode(FriendLeetcode(handle = userHandle, itsMe = true))
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception in storing data at local database",e.toString())
                                        }
                                    }
                                    else if (selectedPlatform == "spoj") {
                                        lastAddedplatform=2
                                        if(currSelectedPlatform==2)sameplatForm=true
                                        spojUrl="https://www.spoj.com/users/"+userHandle
                                        binding.shimmer.visibility=View.VISIBLE
//                                        binding.progressbar.visibility=View.VISIBL
                                        try
                                        {
                                            viewmodel.insertMyPlatform(MyPlatforms("spoj", userHandle))
                                            viewmodel.FriendInsertSpoj(FriendSpoj(handle = userHandle, itsMe = true))
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception in storing data at local database",e.toString())
                                        }
                                    }
                                    else if (selectedPlatform == "codechef") {
                                        lastAddedplatform=3
                                        if(currSelectedPlatform==3)sameplatForm=true
                                        spojUrl="https://www.spoj.com/users/"+userHandle
                                        binding.shimmer.visibility=View.VISIBLE
//                                        binding.progressbar.visibility=View.VISIBL
                                        try
                                        {
                                            viewmodel.insertMyPlatform(MyPlatforms("codechef", userHandle))
                                            viewmodel.FriendInsertCodechef(FriendCodechef(handle = userHandle, itsMe = true))
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception in storing data at local database",e.toString())
                                        }
                                    }
                                    dialog.dismiss()
                                } else {
                                    binding.shimmer.visibility=View.GONE
                                    addPlatformbinding.progressbar.visibility = View.INVISIBLE

                                    if ((response.has("details")&&response.getString("details") == "Invalid username")||(response.has("comment")&&response.getString("comment") == "handles: User with handle $userHandle not found")||(response.has("message")&&response.getString("message") == "user does not exist")) {
                                        addPlatformbinding.userhandle.setError("Invalid user handle")
                                    }

                                }

//                        Log.d("User",response.toString())

                            }, {
                                binding.shimmer.visibility=View.GONE
                                addPlatformbinding.progressbar.visibility = View.INVISIBLE

                                Log.d("VolleyError", it.message.toString())
                                addPlatformbinding.userhandle.setError("User Does not Exist")
                            })

                        queue.add(jsonObject)
                        queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<String?> {

                            Log.d("New Handle Added", "Finished")

                        })


                    }

                }

                dialog.show()
            }
            catch (e:Exception)
            {
                Log.e("Exception while Adding Platform",e.toString())
            }


        }


        return binding.root
    }


    private fun AssignValue() {
        binding.userName.text=userName

        val reference:StorageReference=cloudStorage.reference.child("images").child(auth.uid!!)

        reference.downloadUrl.addOnSuccessListener {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.ic_baseline_account_circle_24_green)

            try {
                Glide.with(requireActivity()).setDefaultRequestOptions(requestOption).load(it.toString()).into(binding.profileImage)

            }
            catch (e:Exception)
            {
                Log.d("Glide Exception",e.message.toString())
            }

        }

    }

    override fun leaderBoardItemSelected(platform: String) {

        Log.d("ItemSeleced function called","Yes")
        binding.shimmer.visibility=View.VISIBLE
        if(platform=="codeforces")
        {
            userAdded=false
            sameplatForm=false
            currSelectedPlatform=0
        }
        else if(platform=="leetcode")
        {
            userAdded=false
            sameplatForm=false
            currSelectedPlatform=1
        }
        else if(platform=="spoj"){
            userAdded=false
            sameplatForm=false
            currSelectedPlatform=2
        }
        else if(platform=="codechef"){
            userAdded=false
            sameplatForm=false
            currSelectedPlatform=3
        }
        Log.d("CurrPlatForm = ","$currSelectedPlatform  $platform" )
        if((currSelectedPlatform==0&&propertyListCf.isEmpty())||(currSelectedPlatform==1&&propertyListLc.isEmpty())||(currSelectedPlatform==2&&propertyListSj.isEmpty())||(currSelectedPlatform==3&&propertyListCc.isEmpty()))
        {

            try {
                viewmodel.MyPlatforms.observe(requireActivity(), Observer { list->

                    Log.d("Listner works","Yes")
                    Log.d("Flags","userAdded $userAdded sameplatform $sameplatForm")
                    if((userAdded&&sameplatForm)||!userAdded)
                    {
                        userAdded=false
                        sameplatForm=false
                        list?.let {
                            if(currSelectedPlatform==0) propertyListCf.clear()
                            else if(currSelectedPlatform==1)propertyListLc.clear()
                            else if(currSelectedPlatform==2)propertyListSj.clear()
                            else propertyListCc.clear()
                            var handle:String=""
                            it.forEach {

                                if(it.platform=="codeforces"&&it.handle!=null)
                                {
                                    codeforcesUrl="https://codeforces.com/profile/"+it.handle
                                }
                                if(it.platform=="leetcode"&&it.handle!=null)
                                {
                                    leetCodeUrl="https://leetcode.com/"+it.handle
                                }
                                if(it.platform=="spoj"&&it.handle!=null)
                                {
                                    spojUrl="https://www.spoj.com/users/"+it.handle
                                }
                                if(it.platform=="codechef"&&it.handle!=null)
                                {
                                    codechefUrl="https://www.codechef.com/users/"+it.handle
                                }
                                if(it.platform==platform&&it.handle!=null)
                                {
                                    Log.d("platform","${it.platform} -> ${it.handle}")
                                    handle=it.handle
                                }

                            }
                            var url:String=""
                            if(handle=="")
                            {
                                val defaultMessage=ArrayList<profile_userinfo>()
                                defaultMessage.add(profile_userinfo("Please Add Your Handle",""))
                                binding.shimmer.visibility=View.GONE

                                try {
                                    infoAdapter.dataupdated(defaultMessage)
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception while calling updateData fun of Adapter",e.toString())
                                }
                            }else
                            {
                                if(platform=="codeforces")
                                {
                                    url="https://codeforces.com/api/user.info?handles="+handle
                                }
                                else if(platform=="leetcode")
                                {
                                    url="https://leetcode-stats-api.herokuapp.com/"+handle
                                }
                                else if(platform=="codechef")
                                {
                                    url = "https://competitive-coding-api.herokuapp.com/api/codechef"+"/" + handle
                                }
                                else{
                                    url = "https://competitive-coding-api.herokuapp.com/api/spoj"+"/" + handle
                                }
                                Log.d("built url",url)
                                makeVolleyRequest(url,platform)
                            }


                        }
                    }else
                    {
                        if(lastAddedplatform==0)propertyListCf.clear()
                        if(lastAddedplatform==1)propertyListLc.clear()
                        if(lastAddedplatform==2)propertyListSj.clear()
                        if(lastAddedplatform==3)propertyListCc.clear()
                        binding.shimmer.visibility=View.GONE
                    }

                })
            }
            catch (e:Exception)
            {
                Log.e("Exception in live data observer",e.toString())
            }

        }
        else{
            try {
                if(currSelectedPlatform==0)infoAdapter.dataupdated(propertyListCf)
                else if(currSelectedPlatform==1)infoAdapter.dataupdated(propertyListLc)
                else if(currSelectedPlatform==2)infoAdapter.dataupdated(propertyListSj)
                else if(currSelectedPlatform==3)infoAdapter.dataupdated(propertyListCc)
                binding.shimmer.visibility=View.GONE
            }
            catch (e:Exception)
            {
                Log.e("Exception while calling updateData fun of Adapter",e.toString())
            }


        }

    }

    private fun makeVolleyRequest(url:String,platform:String)
    {

        val queue = Volley.newRequestQueue(context)
        var jsonObj:JSONObject?=null

        val jsonObject=JsonObjectRequest(Request.Method.GET,url,null,{response->

            if (response.has("status")&&response.getString("status").lowercase() == "success"||response.getString("status") == "OK"){
                Log.d("Response","Ok or Success")
                jsonObj=response
            }

        },{
            Log.d("Error In VolleyRequest While showing details",it.message.toString())
        })
        queue.add(jsonObject)
        queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<String?> {
            if(jsonObj!=null)
            {
                Log.d("Calling supplyDataToAdapter function","Yes")
                SuppltDataToAdapter(jsonObj!!,platform)
            }
            else
            {
                Log.d("JsonObj Is Null","Yes")
                val defaultMessage=ArrayList<profile_userinfo>()
                defaultMessage.add(profile_userinfo("oops Some Error Occurred",""))
                binding.shimmer.visibility=View.GONE
                try {
                    infoAdapter.dataupdated(defaultMessage)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while calling updateData fun of Adapter",e.toString())
                }
            }
        })

    }


    private fun SuppltDataToAdapter(obj:JSONObject,platform:String)
    {
        Log.d("In SupplyDataToAdapter Fun","Yes currselectedPlatform ${currSelectedPlatform} obj : $obj")
        try {
            if(currSelectedPlatform==0&&platform=="codeforces")
            {
                try {
                    val jsonArray=obj.getJSONArray("result")
                    val objectInArray=jsonArray.getJSONObject(0)
                    propertyListCf.add(profile_userinfo("Contribution",objectInArray.getString("contribution").toString()))
                    propertyListCf.add(profile_userinfo("Rating",objectInArray.getString("rating").toString()))
                    propertyListCf.add(profile_userinfo("Maximum Rating",objectInArray.getString("maxRating").toString()))
                    propertyListCf.add(profile_userinfo("Maximum Rank",objectInArray.getString("maxRank").toString()))
                    binding.shimmer.visibility=View.GONE
                    infoAdapter.dataupdated(propertyListCf)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while taking datab from JsonObject of codeforces and calling updateData of Adapter",e.toString())
                }


            }
            else if(currSelectedPlatform==1&&platform=="leetcode")
            {

                try {
                    propertyListLc.add(profile_userinfo("Total Solved Question",obj.getString("totalSolved").toString()))
                    propertyListLc.add(profile_userinfo("Easy Question",obj.getString("easySolved").toString()))
                    propertyListLc.add(profile_userinfo("Medium Question",obj.getString("mediumSolved").toString()))
                    propertyListLc.add(profile_userinfo("Hard Question",obj.getString("hardSolved").toString()))
                    propertyListLc.add(profile_userinfo("Acceptance Rate",obj.getString("acceptanceRate").toString()))
                    propertyListLc.add(profile_userinfo("Ranking",obj.getString("ranking").toString()))
                    propertyListLc.add(profile_userinfo("Contribution Points",obj.getString("contributionPoints").toString()))
                    binding.shimmer.visibility=View.GONE
                    infoAdapter.dataupdated(propertyListLc)
                }
                catch (e:Exception) {
                    Log.e("Exception while taking datab from JsonObject of Leetcode and calling updateData of Adapter",
                        e.toString())
                }
            }
            else if(currSelectedPlatform==2&&platform=="spoj")
            {

                try {
                    propertyListSj.add(profile_userinfo("Points",obj.getString("points").toString()))
                    propertyListSj.add(profile_userinfo("Rank",obj.getString("rank").toString()))
                    binding.shimmer.visibility=View.GONE
                    infoAdapter.dataupdated(propertyListSj)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while taking datab from JsonObject of Spoj and calling updateData of Adapter",e.toString())
                }

            }
            else if(currSelectedPlatform==3&&platform=="codechef")
            {
                val unicode:Int = 0x2B50
                        try {
                    propertyListCc.add(profile_userinfo("Rating",obj.getString("rating").toString()))
                    propertyListCc.add(profile_userinfo("Max Rating",obj.getString("highest_rating").toString()))
                    propertyListCc.add(profile_userinfo("Star",(obj.getString("stars").toString().get(0)+" ${getEmojiByUnicode(unicode)}").toString()))
                    propertyListCc.add(profile_userinfo("Global Rank",obj.getString("global_rank").toString()))
                    propertyListCc.add(profile_userinfo("Country Rank",obj.getString("country_rank").toString()))
                    binding.shimmer.visibility=View.GONE
                    infoAdapter.dataupdated(propertyListCc)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while taking datab from JsonObject of Spoj and calling updateData of Adapter",e.toString())
                }

            }
        }
        catch (e:Exception)
        {
            Log.e("Exception in supplyDataToAdapter fun",e.toString())
        }
    }
    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }
}