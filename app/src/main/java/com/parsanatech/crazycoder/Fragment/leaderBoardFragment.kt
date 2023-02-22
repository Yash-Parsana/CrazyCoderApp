package com.parsanatech.crazycoder.Fragment

import android.app.ActionBar
import android.app.Application
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.core.RepoManager.interrupt
import com.parsanatech.crazycoder.*
import com.parsanatech.crazycoder.Adapters.FriendRemove
import com.parsanatech.crazycoder.Adapters.LeaderBoardRanklistAdapter
import com.parsanatech.crazycoder.Adapters.leaderBoardItemSelected
import com.parsanatech.crazycoder.Adapters.platformAdapterLeaderBoard
import com.parsanatech.crazycoder.databinding.ActivityAddFriendBinding
import com.parsanatech.crazycoder.databinding.FragmentLeaderBoardBinding
import com.parsanatech.crazycoder.models.DbViewModel
import com.parsanatech.crazycoder.models.LeaderBoardRankers
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext
import kotlin.reflect.typeOf


class leaderBoardFragment : Fragment(), leaderBoardItemSelected, FriendRemove {

    lateinit var binding: FragmentLeaderBoardBinding
    lateinit var viewmodel: DbViewModel
    lateinit var ranklistAdapter:LeaderBoardRanklistAdapter
    var userCodechefList=ArrayList<FriendCodechef>()
    val userCodeforcesList=ArrayList<FriendCodeforces>()
    val userLeetcodeList=ArrayList<FriendLeetcode>()
    val userSpojList=ArrayList<FriendSpoj>()
    val ranklistCc=ArrayList<LeaderBoardRankers>()
    val rankersCf=ArrayList<LeaderBoardRankers>()
    val rankersCc=ArrayList<LeaderBoardRankers>()
    val rankersLc=ArrayList<LeaderBoardRankers>()
    val rankersSj=ArrayList<LeaderBoardRankers>()

    var requestsDone=0
    var currPlatform=3
    var adduserRequest=false
    var standAtSamePlatform=false
    var CchandleAddinginBg=false
    var CfhandleAddedinBg=false
    var LchandleAddedinBg=false
    var SjhandleAddedinBg=false
    var fragmenyActive=true
    var LoggedInUserCfHandle=""
    var LoggedInUserLcHandle=""
    var LoggedInUserSjHandle=""
    var LoggedInUserCcHandle=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val platformListforApi = ArrayList<String>()
        platformListforApi.add("codeforces")
        platformListforApi.add("leetcode")
        platformListforApi.add("spoj")
        platformListforApi.add("codechef")

        Log.d("CreatingView","Yes")

        binding = FragmentLeaderBoardBinding.inflate(layoutInflater, container, false)

        val platformAdapter = platformAdapterLeaderBoard(this,1)

        binding.platformrecyclerView.adapter = platformAdapter
        binding.platformrecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        ranklistAdapter = LeaderBoardRanklistAdapter(this)

        binding.rankersRecyclerView.adapter = ranklistAdapter
        binding.rankersRecyclerView.layoutManager = LinearLayoutManager(context)


        try {
            viewmodel = ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance((activity?.applicationContext as Application))).get(
                DbViewModel::class.java)
        }
        catch (e:Exception)
        {
            Log.e("Exception while initializing viewmodel",e.toString())
        }


        leaderBoardItemSelected("codechef")


        fragmenyActive=true

        binding.swaprefresh.setOnRefreshListener {

            if(currPlatform==0)
            {
                userCodeforcesList.clear()
                rankersCf.clear()
                leaderBoardItemSelected("codeforces")
            }
            else if(currPlatform==1)
            {
                userLeetcodeList.clear()
                rankersLc.clear()
                leaderBoardItemSelected("leetcode")

            }
            else if(currPlatform==2)
            {
                userSpojList.clear()
                rankersSj.clear()
                leaderBoardItemSelected("spoj")
            }
            else if(currPlatform==3)
            {
                userCodechefList.clear()
                ranklistCc.clear()
                leaderBoardItemSelected("codechef")
            }
            binding.shimmer.visibility=View.VISIBLE
            binding.swaprefresh.isRefreshing=false
        }


        binding.Addfriend.setOnClickListener {

            try {
                val addFriendbinding = ActivityAddFriendBinding.inflate(layoutInflater)

                addFriendbinding.autoCompleteTextView.inputType = InputType.TYPE_NULL

                // get reference to the string array that we just created
                val plaforms = resources.getStringArray(R.array.platforms)
                // create an array adapter and pass the required parameter
                // in our case pass the context, drop down layout , and array.
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, plaforms)
                // get reference to the autocomplete text view
                // set adapter to the autocomplete tv to the arrayAdapter
                addFriendbinding.autoCompleteTextView.setAdapter(arrayAdapter)

                val dialog = Dialog(requireContext())
                dialog.setContentView(addFriendbinding.root)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//            val selectedValue: String = (addFriendbinding.autoCompleteTextView as AutoCompleteTextView).text.toString()


                var selectedPlatform: String = ""

                try {
                    addFriendbinding.autoCompleteTextView.onItemClickListener =
                        OnItemClickListener { adapterView, view, position, id ->
                            selectedPlatform = platformListforApi.get(position).toString()
                        }
                }
                catch (e:Exception)
                {
                    Log.e("Exception in AddFriend Platform Selection",e.toString())
                }


                addFriendbinding.button.setOnClickListener {

                    addFriendbinding.progressbar.visibility = View.VISIBLE

                    val userHandle = addFriendbinding.userhandle.text.toString().trim()

                    if (selectedPlatform.isEmpty()) {
                        addFriendbinding.autoCompleteTextView.setError("Please select a Platform")
                        addFriendbinding.progressbar.visibility = View.INVISIBLE
                    }
                    if (userHandle.isEmpty()) {
                        addFriendbinding.userhandle.setError("Please Enter Handle")
                        addFriendbinding.progressbar.visibility = View.INVISIBLE
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

                                    addFriendbinding.progressbar.visibility = View.INVISIBLE
                                    if (selectedPlatform == "codeforces") {
                                        adduserRequest=true
                                        if(currPlatform==0)
                                        {
                                            standAtSamePlatform=true
                                            binding.shimmer.visibility=View.VISIBLE
                                            binding.progressbar.visibility=View.VISIBLE
                                        }

                                        try {
                                            viewmodel.FriendInsertCodeforces(FriendCodeforces(handle = userHandle, itsMe = false))

                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception while storing Data in Room Database",e.toString())
                                        }



                                        Log.d("Inserted","Yes")
                                    }
                                    else if (selectedPlatform == "leetcode") {
                                        adduserRequest=true
                                        if(currPlatform==1)
                                        {
                                            standAtSamePlatform=true
                                            binding.shimmer.visibility=View.VISIBLE
                                            binding.progressbar.visibility=View.VISIBLE
                                        }
                                        try {
                                            viewmodel.FriendInsertLeetcode(FriendLeetcode(handle = userHandle, itsMe = false))

                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception while storing Data in Room Database",e.toString())
                                        }


                                    }
                                    else if (selectedPlatform == "spoj") {
                                        adduserRequest=true

                                        if(currPlatform==2)
                                        {
                                            standAtSamePlatform=true
                                            binding.shimmer.visibility=View.VISIBLE
                                            binding.progressbar.visibility=View.VISIBLE
                                        }
                                        try {
                                            viewmodel.FriendInsertSpoj(FriendSpoj(handle = userHandle, itsMe = false))
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception while storing Data in Room Database",e.toString())
                                        }

                                    }
                                    else if(selectedPlatform=="codechef")
                                    {
                                        adduserRequest=true

                                        if(currPlatform==3)
                                        {
                                            standAtSamePlatform=true
                                            binding.shimmer.visibility=View.VISIBLE
                                            binding.progressbar.visibility=View.VISIBLE
                                        }
                                        try {
                                            viewmodel.FriendInsertCodechef(FriendCodechef(handle = userHandle, itsMe = false))
                                        }
                                        catch (e:Exception)
                                        {
                                            Log.e("Exception while storing Data in Room Database",e.toString())
                                        }
                                    }
                                    dialog.dismiss()
                                } else {
                                    binding.shimmer.visibility=View.GONE
                                    addFriendbinding.progressbar.visibility = View.INVISIBLE

                                    if ((response.has("details")&&response.getString("details") == "Invalid username")||(response.has("comment")&&response.getString("comment") == "handles: User with handle $userHandle not found")||(response.has("message")&&response.getString("message") == "user does not exist")) {
                                        addFriendbinding.userhandle.setError("Invalid user handle")
                                    }

                                }

//                        Log.d("User",response.toString())

                            }, {
                                binding.shimmer.visibility=View.GONE
                                addFriendbinding.progressbar.visibility = View.INVISIBLE

                                Log.d("VolleyError", it.message.toString())
                                addFriendbinding.userhandle.setError("User Does not Exist")
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
                Log.e("Exception in Addfriend click",e.toString())
            }
        }


        return binding.root
    }

    override fun leaderBoardItemSelected(platform: String) {
        binding.progressbar.visibility=View.VISIBLE
        binding.shimmer.visibility=View.VISIBLE

        Log.d("Inside leaderBoardIteamSelcted fun with Platform",platform)
        try {
            requireActivity().runOnUiThread(Runnable {
                Log.d("Screen Jam","Yes")
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            })
        }
        catch (e:Exception)
        {
            Log.e("Exception while Loacking screen in leaderBoardIteamSelected Fun",e.toString())
        }

        if(platform=="codechef")
        {
            currPlatform=3
            adduserRequest=false
            standAtSamePlatform=false

            try {
                val params:LinearLayout.LayoutParams= LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight=2F
                binding.judgingCriteria.layoutParams=params
                binding.judgingCriteria.setTextSize(15F)
                binding.judgingCriteria.text="Rating"
            }
            catch (e:Exception)
            {
                Log.e("Exception while changing content of Ui in codechef",e.toString())
            }

            if(userCodechefList.isEmpty()||CchandleAddinginBg)
            {
                try {
                    CchandleAddinginBg=false
                    viewmodel.FriendCodechefHandles.observe(viewLifecycleOwner, Observer { list ->


                        if(((adduserRequest&&standAtSamePlatform)||!adduserRequest)&&fragmenyActive) //it checks added user platform and current platform is same to avoid unnessery reading of data from room database fragmeyactive prevent call if handle added through profile fragment
                        {

                            adduserRequest=false
                            standAtSamePlatform=false
                            list?.let {
                                Log.d("Flags"," adduserRequest : ${adduserRequest} , standAtSamePlatform : ${standAtSamePlatform}")

                                userCodechefList.clear()
                                rankersCc.clear()

                                userCodechefList.addAll(it)

                                Log.d("codechefData",it.size.toString())
                                Log.d("Totaluser indatabase : ",it.size.toString() )

                            }
                            Log.d("codechef List in itemselected function : ",userCodechefList.size.toString() )
                            if(userCodechefList.isEmpty())
                            {
                                binding.shimmer.visibility=View.GONE
                                binding.progressbar.visibility=View.INVISIBLE
                                rankersCc.add(LeaderBoardRankers("-",0,false))

                                try {
                                    ranklistAdapter.updateData(rankersCc)
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception in calling updatedata fun of adapter",e.toString())
                                }
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                            requestsDone=userCodechefList.size
                            renderUser(platform)
                        }
                        else
                        {
                            CchandleAddinginBg=true

                        }

                    })
                }
                catch (e:Exception)
                {
                    Log.e("Exception in live observer",e.toString())
                }

            }
            else
            {
                binding.shimmer.visibility=View.GONE
                binding.progressbar.visibility=View.INVISIBLE
                try {
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ranklistAdapter.updateData(rankersCc)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while unlocking ui and updateData of Adapter",e.toString())
                }

            }

        }
        else if (platform == "codeforces") {
            currPlatform=0
            adduserRequest=false
            standAtSamePlatform=false

            try {
                val params:LinearLayout.LayoutParams= LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight=2F
                binding.judgingCriteria.layoutParams=params
                binding.judgingCriteria.setTextSize(15F)
                binding.judgingCriteria.text="Rating"
            }
            catch (e:Exception)
            {
                Log.e("Exception while changing content of Ui in codeforces",e.toString())
            }


            if(userCodeforcesList.isEmpty()||CfhandleAddedinBg)
            {
                try {
                    CfhandleAddedinBg=false
                    viewmodel.FriendCodeforcesHandles.observe(viewLifecycleOwner, Observer { list ->


                        if(((adduserRequest&&standAtSamePlatform)||!adduserRequest)&&fragmenyActive) //it checks added user platform and current platform is same to avoid unnessery reading of data from room database fragmeyactive prevent call if handle added through profile fragment
                        {

                            adduserRequest=false
                            standAtSamePlatform=false
                            list?.let {
                                Log.d("Flags"," adduserRequest : ${adduserRequest} , standAtSamePlatform : ${standAtSamePlatform}")

                                userCodeforcesList.clear()
                                rankersCf.clear()

                                userCodeforcesList.addAll(it)

                                Log.d("codeforcesData",it.size.toString())
                                Log.d("Totaluser indatabase : ",it.size.toString() )

                            }
                            Log.d("codeforces List in itemselected function : ",userCodeforcesList.size.toString() )
                            if(userCodeforcesList.isEmpty())
                            {
                                binding.shimmer.visibility=View.GONE
                                binding.progressbar.visibility=View.INVISIBLE
                                rankersCf.add(LeaderBoardRankers("-",0,false))

                                try {
                                    ranklistAdapter.updateData(rankersCf)
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception in calling updatedata fun of adapter",e.toString())
                                }
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                            requestsDone=1
                            renderUser(platform)
                        }
                        else
                        {
                            CfhandleAddedinBg=true

                        }

                    })
                }
                catch (e:Exception)
                {
                    Log.e("Exception in live observer",e.toString())
                }

            }
            else
            {
                binding.shimmer.visibility=View.GONE
                binding.progressbar.visibility=View.INVISIBLE
                try {
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ranklistAdapter.updateData(rankersCf)
                }
                catch (e:Exception)
                {
                    Log.e("Exception while unlocking ui and updateData of Adapter",e.toString())
                }

            }

        }
        else if (platform == "leetcode") {
            currPlatform=1
            adduserRequest=false
            standAtSamePlatform=false

            try
            {
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight = 3F
                binding.judgingCriteria.layoutParams = params
                binding.judgingCriteria.setTextSize(13F)
                binding.judgingCriteria.text = "Solved Questions"
            }
            catch (e:Exception)
            {
                Log.e("Exception while changing content of Ui in codeforces",e.toString())
            }

            if(userLeetcodeList.isEmpty()||LchandleAddedinBg)
            {
                try
                {
                    LchandleAddedinBg = false
                    viewmodel.FriendLeetcodeHandles.observe(requireActivity(), Observer { list ->

                        if (((adduserRequest && standAtSamePlatform) || !adduserRequest) && fragmenyActive) //it checks added user platform and current platform is same to avoid unnessery reading of data from room database fragmeyactive prevent call if handle added through profile fragment
                        {
                            Log.d("Flags",
                                " adduserRequest : ${adduserRequest} , standAtSamePlatform : ${standAtSamePlatform}")

                            adduserRequest = false
                            standAtSamePlatform = false
                            list?.let {
                                userLeetcodeList.clear()
                                rankersLc.clear()

                                userLeetcodeList.addAll(it)
                                Log.d("Totaluser indatabase : ${Thread.currentThread().name}",
                                    it.size.toString())
                            }
                            if (userLeetcodeList.isEmpty()) {
                                binding.shimmer.visibility = View.GONE
                                binding.progressbar.visibility = View.INVISIBLE
                                rankersLc.add(LeaderBoardRankers("-", 0, false))
                                try {
                                    ranklistAdapter.updateData(rankersLc)
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception while calling updatedata fun of Adapter",e.toString())
                                }
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                            requestsDone = userLeetcodeList.size
                            Log.d("Above renderUser Call", "Yes")
                            renderUser(platform)
                        } else {
                            LchandleAddedinBg = true

                        }

                    })
                }
                catch (e:Exception)
                {
                    Log.e("Exception in Live data observer",e.toString())
                }

            }
            else{
                binding.shimmer.visibility=View.GONE
                binding.progressbar.visibility=View.INVISIBLE

                try {
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ranklistAdapter.updateData(rankersLc)
                }
                catch (e:Exception)
                {
                    Log.e("Exception While unlocking Ui and updatedata",e.toString())
                }

            }


        }
        else if (platform == "spoj") {

            currPlatform=2
            adduserRequest=false
            standAtSamePlatform=false

            try {
                val params:LinearLayout.LayoutParams= LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.weight=2F
                binding.judgingCriteria.layoutParams=params
                binding.judgingCriteria.setTextSize(15F)
                binding.judgingCriteria.text="Rank"
            }
            catch (e:Exception)
            {
                Log.e("Exception while changing content of Ui in codeforces",e.toString())
            }

            Log.d("FunctionCalled for spoj","Yes")
            if(userSpojList.isEmpty()||SjhandleAddedinBg)
            {
                try {
                    SjhandleAddedinBg=false
                    viewmodel.FriendSpojHandles.observe(requireActivity(), Observer { list ->

                        if(((adduserRequest&&standAtSamePlatform)||!adduserRequest)&&fragmenyActive) //it checks added user platform and current platform is same to avoid unnessery reading of data from room database fragmeyactive prevent call if handle added through profile fragment
                        {
                            Log.d("Flags"," adduserRequest : ${adduserRequest} , standAtSamePlatform : ${standAtSamePlatform}")
                            adduserRequest=false
                            standAtSamePlatform=false
                            list?.let {
                                userSpojList.clear()
                                rankersSj.clear()

                                Log.d("Sizes","spoj it : ${it.size}")

                                userSpojList.addAll(it)
                            }
                            if(userSpojList.isEmpty())
                            {
                                binding.shimmer.visibility=View.GONE
                                binding.progressbar.visibility=View.INVISIBLE
                                rankersSj.add(LeaderBoardRankers("-",0,false))

                                try {
                                    ranklistAdapter.updateData(rankersSj)
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception while calling updatedata fun of Adapter",e.toString())
                                }
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                            requestsDone=userSpojList.size
                            renderUser(platform)
                        }
                        else
                        {
                            SjhandleAddedinBg=true

                        }

                    })

                }
                catch (e:Exception)
                {
                    Log.e("Exception in Live data observer",e.toString())
                }


            }
            else{
                binding.shimmer.visibility=View.GONE
                binding.progressbar.visibility=View.INVISIBLE

                try {
                    requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    ranklistAdapter.updateData(rankersSj)
                }
                catch (e:Exception)
                {
                    Log.e("Exception While unlocking Ui and updatedata",e.toString())
                }
            }
        }


    }


    private fun renderUser(platform: String)
    {

        try {

            Thread{
                if(platform=="codeforces")
                {
//               Formate : var url="https://codeforces.com/api/user.info?handles=tourist;YashParsana"

                    var urlCf="https://codeforces.com/api/user.info?handles="
                    for(i in 0 until userCodeforcesList.size)
                    {
                        val handle:String=userCodeforcesList.get(i).handle
                        if(userCodeforcesList[i].itsMe==true) LoggedInUserCfHandle=userCodeforcesList[i].handle
                        if(i!=userCodeforcesList.size-1)
                            urlCf+=handle+";"
                        else
                        {
                            urlCf+=handle
                        }
                    }
                    makeVolleyRequest(0,"",urlCf)

                }
                else if(platform=="leetcode")
                {
                    val LCurl="https://leetcode-stats-api.herokuapp.com/"
                    for(i in 0 until userLeetcodeList.size)
                    {
                        if(userLeetcodeList[i].itsMe==true) LoggedInUserLcHandle=userLeetcodeList[i].handle
                        val handle:String=userLeetcodeList.get(i).handle
                        makeVolleyRequest(1,handle,LCurl+handle)
                    }
                }
                else if(platform=="spoj")
                {
                    var url="https://competitive-coding-api.herokuapp.com/api/"+platform
                    for(i in 0 until userSpojList.size)
                    {
                        if(userSpojList[i].itsMe==true) LoggedInUserSjHandle=userSpojList[i].handle
                        val handle:String=userSpojList.get(i).handle
                        makeVolleyRequest(2,handle,url+"/"+handle)
                    }
                }
                else if(platform=="codechef")
                {
                    var url="https://competitive-coding-api.herokuapp.com/api/"+platform
                    for(i in 0 until userCodechefList.size)
                    {
                        if(userCodechefList[i].itsMe==true) LoggedInUserCcHandle=userCodechefList[i].handle
                        val handle:String=userCodechefList.get(i).handle
                        makeVolleyRequest(3,handle,url+"/"+handle)
                    }
                }
                while(requestsDone>0)
                {
                    if(!fragmenyActive)
                    {
                        if(platform=="codeforces")
                        {
                            rankersCf.clear()
                            userCodeforcesList.clear()
                        }
                        else if(platform=="leetcode")
                        {
                            rankersLc.clear()
                            userLeetcodeList.clear()
                        }
                        else if(platform=="spoj")
                        {
                            rankersSj.clear()
                            userSpojList.clear()
                        }
                        else if(platform=="codechef")
                        {
                            rankersCc.clear()
                            userCodechefList.clear()
                        }
                        break
                    }
                    Thread.sleep(50)
                }
//            Log.d("AfterDelay rankes size : ${rankers.isEmpty()}","Yes")
                if(platform=="codeforces"&&fragmenyActive)
                {
                    if(rankersCf.isNotEmpty())
                    {
                        try {
                            val sorted=rankersCf.sortedByDescending{ it.rank }
                            rankersCf.clear()
                            rankersCf.addAll(sorted)
                            supplyDataToAdapter()
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception in sorting of data",e.toString())
                        }


                    }
                    else
                    {
                        disableVisibility()
                        rankersCf.add(LeaderBoardRankers("-",0,false))
                        supplyDataToAdapter()

                    }


                }
                else if(platform=="leetcode"&&fragmenyActive)
                {
                    if(rankersLc.isNotEmpty())
                    {
                        try {
                            val sorted=rankersLc.sortedByDescending{ it.rank }
                            rankersLc.clear()
                            rankersLc.addAll(sorted)
                            supplyDataToAdapter()
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception in sorting of data",e.toString())
                        }

                    }
                    else
                    {
                        disableVisibility()
                        rankersLc.add(LeaderBoardRankers("-",0,false))
                        supplyDataToAdapter()

                    }

                }

                if(platform=="spoj"&&fragmenyActive)
                {
                    if(rankersSj.isNotEmpty())
                    {
                        try
                        {
                            val sorted = rankersSj.sortedBy { it.rank }
                            rankersSj.clear()
                            rankersSj.addAll(sorted)
                            supplyDataToAdapter()
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception in sorting of data",e.toString())
                        }
                    }
                    else
                    {
                        disableVisibility()
                        rankersSj.add(LeaderBoardRankers("-",0,false))
                        supplyDataToAdapter()
                    }
                }
                if(platform=="codechef"&&fragmenyActive)
                {
                    if(rankersCc.isNotEmpty())
                    {
                        try
                        {
                            val sorted = rankersCc.sortedByDescending { it.rank }
                            rankersCc.clear()
                            rankersCc.addAll(sorted)
                            supplyDataToAdapter()
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception in sorting of data",e.toString())
                        }
                    }
                    else
                    {
                        disableVisibility()
                        rankersCc.add(LeaderBoardRankers("-",0,false))
                        supplyDataToAdapter()
                    }
                }

            }.start()
        }
        catch (e:Exception)
        {
            Log.e("Exception in Parallel thread",e.toString())
        }


    }
    fun disableVisibility()
    {
        try
        {
            requireActivity().runOnUiThread(Runnable {

                binding.shimmer.visibility = View.GONE
                binding.progressbar.visibility = View.INVISIBLE

            })
        }
        catch (e:Exception)
        {
            Log.e("Error in disableVisiblity Fun",e.toString())
        }
    }

    fun supplyDataToAdapter()
    {
        try {

            requireActivity().runOnUiThread(Runnable {
                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.d("IamHere","Yes ${Thread.currentThread().name}")
                binding.progressbar.visibility=View.INVISIBLE
                binding.shimmer.visibility=View.GONE
                if(currPlatform==0)
                {
                    ranklistAdapter.updateData(rankersCf)
                }
                else if(currPlatform==1)
                {
                    ranklistAdapter.updateData(rankersLc)
                }
                else if(currPlatform==2)
                {
                    ranklistAdapter.updateData(rankersSj)
                }
                else if(currPlatform==3)
                {
                    ranklistAdapter.updateData(rankersCc)
                }

            })
        }
        catch (e:Exception)
        {
            Log.e("Exception while releasing ui",e.toString())
        }


    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeVolleyRequest(platform:Int, userHandle:String, url:String)
    {
        try {
            GlobalScope.launch(Dispatchers.IO) {

                Log.d("Thread corutine", Thread.currentThread().name)

                val queue = Volley.newRequestQueue(requireActivity())

                Log.d("URL",url)

                val jsonObj=JsonObjectRequest(Request.Method.GET,url,null,{response->

                    Log.d("ResponseGot","Yes")

                    if(response.has("status")&&response.getString("status").lowercase()=="success"||response.getString("status").lowercase()=="ok")
                    {
                        if(currPlatform==0&&platform==0)
                        {
                            val friends=response.getJSONArray("result")
                            for(i in 0 until friends.length())
                            {
                                try {
                                    Log.d("LoggedinuserCfhandle : $LoggedInUserCfHandle"," userHandle $userHandle")
                                    val obj=friends.getJSONObject(i)
                                    if(obj.has("rating"))
                                    {
                                        val currhandle=obj.getString("handle").toString()
                                        rankersCf.add(LeaderBoardRankers(currhandle,obj.getString("rating").toInt(),LoggedInUserCfHandle==currhandle))
                                    }
                                    else{
                                        val currhandle=obj.getString("handle").toString()
                                        rankersCf.add(LeaderBoardRankers(currhandle,0,currhandle==userHandle))
                                    }
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception while working JsonObject",e.toString())
                                }

                            }
                            requestsDone--

                        }
                        else if(currPlatform==1&&platform==1)
                        {
                            if(response.has("totalSolved"))
                            {
                                rankersLc.add(LeaderBoardRankers(userHandle,response.getString("totalSolved").toInt(),LoggedInUserLcHandle==userHandle))
                            }
                            else
                            {
                                rankersLc.add(LeaderBoardRankers(userHandle,0,LoggedInUserLcHandle==userHandle))
                            }
                            requestsDone--

                        }
                        else if(currPlatform==2&&platform==2)
                        {
                            if(response.has("rank"))
                                rankersSj.add(LeaderBoardRankers(userHandle,response.getString("rank").toInt(),LoggedInUserSjHandle==userHandle))
                            else
                                rankersSj.add(LeaderBoardRankers(userHandle,0,LoggedInUserSjHandle==userHandle))
                            requestsDone--

                        }
                        else if(currPlatform==3&&platform==3)
                        {
                            if(response.has("rating"))
                                rankersCc.add(LeaderBoardRankers(userHandle,response.getString("rating").toInt(),LoggedInUserCcHandle==userHandle))
                            else
                                rankersCc.add(LeaderBoardRankers(userHandle,0,LoggedInUserCcHandle==userHandle))

                            requestsDone--

                        }
                    }
                    else
                    {
                        Log.d("Unsuccess","${response.toString()} => ${userHandle}")
                        requestsDone--
                    }

                },{
                    if(platform==currPlatform)
                    {
                        requestsDone--
                    }
                    Log.e("Error in ranklist",it.toString()+" "+url)
                })
                queue.add(jsonObj)
                queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<String?> {
                    Log.d("status", "Rest Request $requestsDone")

                })

            }
        }
        catch (e:Exception)
        {
            Log.e("Excepton in coroutine",e.toString())
        }
    }

    override fun friendremoved(handle: String) {

        if(currPlatform==0)
        {
            Log.d("Deleting Status","${userCodeforcesList.size} $handle")
            var obj:FriendCodeforces?=null
            for(i in userCodeforcesList)
            {
                println("$handle  ${i.handle}")
                if(i.handle.lowercase()==handle.lowercase())
                {
                    obj=i
                    break
                }
            }
            Log.d("Above Delete CodeforcesHandle","Yes")
            try{
                viewmodel.FriendDeleteCodeforces(obj!!)
            }
            catch (e:Exception)
            {
                Log.e("Exception while deleting codeforces user from localDataBase",e.toString())
            }
            binding.progressbar.visibility=View.VISIBLE
            binding.shimmer.visibility=View.VISIBLE
        }
        else if(currPlatform==1)
        {
            var obj:FriendLeetcode?=null
            for(i in userLeetcodeList)
            {
                if(i.handle==handle)
                {
                    obj=i
                    break
                }
            }
            try{
                viewmodel.FriendDeleteLeetcode(obj!!)
            }
            catch (e:Exception)
            {
                Log.e("Exception while deleting LeetCode user from localDataBase",e.toString())
            }
            binding.progressbar.visibility=View.VISIBLE
            binding.shimmer.visibility=View.VISIBLE


        }
        else if(currPlatform==2)
        {
            var obj:FriendSpoj?=null
            for(i in userSpojList)
            {
                if(i.handle==handle)
                {
                    obj=i
                    break
                }
            }
            try {
                viewmodel.FriendDeleteSpoj(obj!!)
            }
            catch (e:Exception)
            {
                Log.e("Exception while deleting Spoj user from localDataBase",e.toString())
            }
            binding.progressbar.visibility=View.VISIBLE
            binding.shimmer.visibility=View.VISIBLE


        }
        else if(currPlatform==3)
        {
            var obj:FriendCodechef?=null
            for(i in userCodechefList)
            {
                if(i.handle==handle)
                {
                    obj=i
                    break
                }
            }
            try {
                viewmodel.FriendDeleteCodechef(obj!!)
            }
            catch (e:Exception)
            {
                Log.e("Exception while deleting Codechef user from localDataBase",e.toString())
            }
            binding.progressbar.visibility=View.VISIBLE
            binding.shimmer.visibility=View.VISIBLE


        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("Stopping LeaderBoard Fragment","Yes")
        fragmenyActive=false
    }

}
