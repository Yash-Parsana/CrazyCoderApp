package com.parsanatech.crazycoder.Fragment

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.parsanatech.crazycoder.Adapters.ImplementReminder
import com.parsanatech.crazycoder.Adapters.PlatformAdapter
import com.parsanatech.crazycoder.Adapters.contestInfoAdapter
import com.parsanatech.crazycoder.Adapters.platFormSelection
import com.parsanatech.crazycoder.databinding.FragmentContestsBinding
import com.parsanatech.crazycoder.models.contestModel
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class contestsFragment : Fragment(), platFormSelection, ImplementReminder {

    var status=false //status shows status of contest like upcoming and ongoing false=upcomig true=ongoing
    lateinit var binding:FragmentContestsBinding
    var lastSelectedPlatform:String="at_coder"
    val mcontestJsonList=ArrayList<JSONObject>()
    lateinit var Contestsadapter:contestInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding=FragmentContestsBinding.inflate(layoutInflater,container,false)

        val platformAdpater=PlatformAdapter(this)

        binding.platformrecyclerView.adapter=platformAdpater
        binding.platformrecyclerView.layoutManager= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


        try {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);// if not touchable flag remains in leaderboard then clear it

        }
        catch (e:Exception)
        {
            Log.e("Exception while loking Ui",e.toString())
        }


        Contestsadapter=contestInfoAdapter(this)
        binding.contestRecyclerView.adapter=Contestsadapter
        binding.contestRecyclerView.layoutManager=LinearLayoutManager(context)

        platformselected(lastSelectedPlatform)


        binding.status.setOnClickListener {

            Log.d("Status changed","F1")
            if(binding.status.text.toString()=="Upcoming")
            {
                status=true
                binding.status.text="Ongoing"
            }
            else{
                status=false
                binding.status.text="Upcoming"
            }
            statusChanged()

        }


        return binding.root

    }

    fun statusChanged() {

        val contestList=ArrayList<contestModel>()

        Log.d("statusChanged",mcontestJsonList.size.toString())

        for (i in 0 until mcontestJsonList.size) {

            try {
                val currobj: JSONObject = mcontestJsonList.get(i)

                Log.d("status : ","${currobj.getString("start_time")}  ${currobj.getString("end_time")}")

                if (status && currobj.getString("status") == "ongoing") {

                    Log.d("Status","Coding")

 /*                   if (lastSelectedPlatform != "code_chef") {
                        Log.d("platform","Codechef")
                        try {
                            val startTime = TimeFormate(currobj.getString("start_time"))
                            val endTime = TimeFormate(currobj.getString("end_time"))
                            val name = currobj.getString("name").toString()


                            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                            val sTime = formatter.parse(startTime) as Date


                            val contest = contestModel(name, startTime, endTime,sTime.time)
                            contestList.add(contest)
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while Time formating",e.toString())
                        }

                    } else {
                        Log.d("Another Platform","Yes")
                        try
                        {
                            var St = currobj.getString("start_time")
                            var Et = currobj.getString("end_time")
                            var timeStamp:Long=0



                            try {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                val value = formatter.parse(St)
                                val dateFormatter =
                                    SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                                dateFormatter.timeZone = TimeZone.getDefault()
                                St = dateFormatter.format(value)

                                timeStamp=value.time.toLong()
//                                    Log.d("Final date in codechef : ",St );
                            } catch (e: java.lang.Exception) {
                                St="-"
                            }
                            try {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                val value = formatter.parse(Et)
                                val dateFormatter =
                                    SimpleDateFormat("dd-MM-yyyy\nhh:mm a") //this format changeable
                                dateFormatter.timeZone = TimeZone.getDefault()
                                Et = dateFormatter.format(value)

//                                    Log.d("Final date in codechef : ",Et );
                            } catch (e: java.lang.Exception) {
                                Et="-"
                            }
                            Log.d("TimeStamp",timeStamp.toString())
                            val name = currobj.getString("name").toString()
                            val contest = contestModel(name, St, Et,timeStamp)
                            contestList.add(contest)
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while Time formating",e.toString())
                        }
                    } */
                    val startTime = TimeFormate(currobj.getString("start_time")).toString()
                    val endTime = TimeFormate(currobj.getString("end_time")).toString()


                    val name = currobj.getString("name").toString()
                    val contest = contestModel(name, startTime, endTime,currobj.getString("start_time").toLong())
                    Log.d("mycontest: ",contest.toString())
                    contestList.add(contest)


                } else if (!status && currobj.getString("status") == "upcoming") {

                    Log.d("status","upcoming")
  /*                  if (lastSelectedPlatform != "code_chef") {
                        Log.d("Platform","Codechef")
                        try {
                            val startTime = TimeFormate(currobj.getString("start_time"))
                            val endTime = TimeFormate(currobj.getString("end_time"))
                            val name = currobj.getString("name").toString()


                            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                            val sTime = formatter.parse(startTime) as Date



                            val contest = contestModel(name, startTime, endTime,sTime.time)
                            contestList.add(contest)
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while Time formating",e.toString())
                        }



                    } else {
                        Log.d("Another Platform","Yes")
                        try
                        {
                            var St = currobj.getString("start_time")
                            var Et = currobj.getString("end_time")
                            var timeStamp:Long=0



                            try {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                val value = formatter.parse(St)
                                val dateFormatter =
                                    SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                                dateFormatter.timeZone = TimeZone.getDefault()
                                St = dateFormatter.format(value)

                                timeStamp=value.time.toLong()
//                                    Log.d("Final date in codechef : ",St );
                            } catch (e: java.lang.Exception) {
                                St="-"
                            }
                            try {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                val value = formatter.parse(Et)
                                val dateFormatter =
                                    SimpleDateFormat("dd-MM-yyyy\nhh:mm a") //this format changeable
                                dateFormatter.timeZone = TimeZone.getDefault()
                                Et = dateFormatter.format(value)

//                                    Log.d("Final date in codechef : ",Et );
                            } catch (e: java.lang.Exception) {
                                Et="-"
                            }
                            Log.d("TimeStamp",timeStamp.toString())
                            val name = currobj.getString("name").toString()
                            val contest = contestModel(name, St, Et,timeStamp)
                            contestList.add(contest)
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while Time formating",e.toString())
                        }


                    }*/
                    val startTime = TimeFormate(currobj.getString("start_time").toString())
                    val endTime = TimeFormate(currobj.getString("end_time").toString())

                    val name = currobj.getString("name").toString()
                    val contest = contestModel(name, startTime, endTime,currobj.getString("start_time").toLong())
                    Log.d("mycontest: ",contest.toString())
                    contestList.add(contest)
                }
            }
            catch (e:Exception)
            {
                Log.e("Exception in statuschanged fun",e.toString())
            }

        }

        if(contestList.isEmpty())
        {
            Log.d("ContestList is Empty",contestList.isEmpty().toString())
            if(status==true)
            {
                val noContest=contestModel("No Running contests","-","-",0)
                contestList.add(noContest)
            }
            else{
                val noContest=contestModel("No Upcoming contests","-","-",0)
                contestList.add(noContest)
            }

        }
        try {
            val sortedlist=contestList.sortedBy { it.timeStamp }
            contestList.clear()
            contestList.addAll(sortedlist)

            Contestsadapter.dataupdated(contestList)
        }
        catch (e:Exception)
        {
            Log.e("Exception while updating Adapter Data",e.toString())
        }
    }

    override fun platformselected(platform:String)
    {
        binding.shimmer.visibility=View.VISIBLE
        binding.progressbar.visibility=View.VISIBLE
        lastSelectedPlatform=platform

        mcontestJsonList.clear()

        val baseurl="https://master--glittering-monstera-c8038d.netlify.app/schedule/"

        val contestList=ArrayList<contestModel>()

        val finalurl=baseurl+platform
        Log.d("FinalURL ",finalurl)
        val queue = Volley.newRequestQueue(context)
        Log.d("makingRequest","Yes")
        val jsonArrayobj=JsonArrayRequest(Request.Method.GET,finalurl,null,{ response->

            Log.d("Response got Length : ",response.length().toString())
            for(i in 0 until response.length())
            {
                try {
                    val currobj:JSONObject=response.getJSONObject(i)

                    mcontestJsonList.add(currobj)

                    if(status&&currobj.getString("status")=="ongoing")
                    {

/*                        if(platform!="code_chef")
                        {
                            try {
                                val startTime=TimeFormate(currobj.getString("start_time"))
                                val endTime=TimeFormate(currobj.getString("end_time"))
                                val name=currobj.getString("name").toString()

                                val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                                val sTime = formatter.parse(startTime) as Date


                                val contest=contestModel(name,startTime,endTime,sTime.time)
                                contestList.add(contest)
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception while Time formating",e.toString())
                            }
                        }
                        else{
                            try
                            {
                                var St = currobj.getString("start_time")
                                var Et = currobj.getString("end_time")
                                var timeStamp:Long=0



                                try {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                                    val value = formatter.parse(St)
                                    val dateFormatter =
                                        SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                                    dateFormatter.timeZone = TimeZone.getDefault()
                                    St = dateFormatter.format(value)

                                    timeStamp=value.time.toLong()
//                                    Log.d("Final date in codechef : ",St );
                                } catch (e: java.lang.Exception) {
                                    St="-"
                                }
                                try {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                                    val value = formatter.parse(Et)
                                    val dateFormatter =
                                        SimpleDateFormat("dd-MM-yyyy\nhh:mm a") //this format changeable
                                    dateFormatter.timeZone = TimeZone.getDefault()
                                    Et = dateFormatter.format(value)

//                                    Log.d("Final date in codechef : ",Et );
                                } catch (e: java.lang.Exception) {
                                    Et="-"
                                }
                                Log.d("TimeStamp",timeStamp.toString())
                                val name = currobj.getString("name").toString()
                                val contest = contestModel(name, St, Et,timeStamp)
                                contestList.add(contest)
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception while Time formating",e.toString())
                            }

                        } */
                        try{
                            val startTime = TimeFormate(currobj.getString("start_time").toString())
                            val endTime = TimeFormate(currobj.getString("end_time").toString())


                            val name = currobj.getString("name").toString()
                            val contest = contestModel(name, startTime, endTime,currobj.getString("start_time").toLong())
                            Log.d("mycontest: ",contest.toString())
                            contestList.add(contest)
                        }
                        catch(e:Exception)
                        {
                            Log.e("Exception while processing data in contest page at line ${e.getStackTrace()[0].getLineNumber()} : ",e.toString())
                        }

                    }
                    else if(!status&&currobj.getString("status")=="upcoming")
                    {
                        Log.d("Temp...","in upcoming")
/*                        if(platform!="code_chef")
                        {
                            try
                            {
                                val startTime = TimeFormate(currobj.getString("start_time"))
                                val endTime = TimeFormate(currobj.getString("end_time"))
                                val name = currobj.getString("name").toString()

                                val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy\nhh:mm a")
                                val sTime = formatter.parse(startTime) as Date


                                val contest = contestModel(name, startTime, endTime,sTime.time)
                                contestList.add(contest)
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception while Time formating",e.toString())
                            }
                        }
                        else{
                            try
                            {
                                var St = currobj.getString("start_time")
                                var Et = currobj.getString("end_time")
                                var timeStamp:Long=0



                                try {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                                    val value = formatter.parse(St)
                                    val dateFormatter =
                                        SimpleDateFormat("dd-MM-yyyy\nhh:mm a") //this format changeable
                                    dateFormatter.timeZone = TimeZone.getDefault()
                                    St = dateFormatter.format(value)

                                    timeStamp=value.time.toLong()
//                                    Log.d("Final date in codechef : ",St );
                                } catch (e: java.lang.Exception) {
                                    St="-"
                                }
                                try {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                                    val value = formatter.parse(Et)
                                    val dateFormatter =
                                        SimpleDateFormat("dd-MM-yyyy\nhh:mm a") //this format changeable
                                    dateFormatter.timeZone = TimeZone.getDefault()
                                    Et = dateFormatter.format(value)

//                                    Log.d("Final date in codechef : ",Et );
                                } catch (e: java.lang.Exception) {
                                    Et="-"
                                }
                                Log.d("TimeStamp",timeStamp.toString())
                                val name = currobj.getString("name").toString()
                                val contest = contestModel(name, St, Et,timeStamp)
                                contestList.add(contest)
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception while Time formating",e.toString())
                            }
                        } */

                        //TimeStamp to Date & Time :
                        try{
                            val startTime = TimeFormate(currobj.getString("start_time").toString())
                            val endTime = TimeFormate(currobj.getString("end_time").toString())
//
                            val name = currobj.getString("name").toString()
                            val contest = contestModel(name, startTime, endTime,currobj.getString("start_time").toLong())
                            Log.d("mycontest: ",contest.toString())
                            contestList.add(contest)
                        }
                        catch(e:Exception)
                        {
                            Log.e("Exception while processing data in contest page at line ${e.getStackTrace()[0].getLineNumber()} : ",e.toString())
                        }

                    }
                }
                catch (e:Exception)
                {
                    Log.e("Exception in successful Volley Request",e.toString())
                }


            }

        },{
            Log.d("vollyError",it.message.toString())
        })
        queue.add(jsonArrayobj)


        queue.addRequestFinishedListener(RequestQueue.RequestFinishedListener<String?> {

            if(contestList.isEmpty())
            {
                Log.d("ContestList is Empty","Yes")
                if(status==true)
                {
                    val noContest=contestModel("No Running contests","-","-",0)
                    contestList.add(noContest)
                }
                else{
                    val noContest=contestModel("No Upcoming contests","-","-",0)
                    contestList.add(noContest)
                }

            }
            try {
                val sortedlist=contestList.sortedBy { it.timeStamp }
                contestList.clear()
                contestList.addAll(sortedlist)
                Contestsadapter.dataupdated(contestList)
                binding.shimmer.visibility=View.GONE
                binding.progressbar.visibility=View.INVISIBLE
            }
            catch (e:Exception)
            {
                Log.e("Error while calling DataUpdate fun",e.toString())
            }
        })


    }

    private fun TimeFormate(string: String): String {

        var finaltime: String=""
//        try
//        {
//            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
//                Locale.getDefault())
//            val currentLocalTime: Date = calendar.getTime()
//            val Localdate: DateFormat = SimpleDateFormat("Z")
//            val localTimeZone = Localdate.format(currentLocalTime)
//
//
//
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
//            inputFormat.timeZone= TimeZone.getTimeZone("GMT")
//            val outputFormat = SimpleDateFormat("dd-MM-yyyy\nhh:mm a", Locale.ENGLISH)
//            val locatTimeZoneInGMT=("GMT"+localTimeZone.slice(0..2)+":"+localTimeZone.slice(3..4))
//            outputFormat.timeZone = TimeZone.getTimeZone(locatTimeZoneInGMT)
//
//            Log.d("TimeZone ",locatTimeZoneInGMT)
//
//
//            val date: Date = inputFormat.parse(string)!!
//            finaltime= outputFormat.format(date)
//
//        }
//        catch (e:Exception)
//        {
//            Log.e("Exception in TimeFormat Fun",e.toString())
//        }
        try{
//            val timeStamp=string.toLong()
//
//            val time=SimpleDateFormat("hh:mm a").format(timeStamp)
//            val date=SimpleDateFormat("dd/MM/yyyy").format(timeStamp)
//            finaltime=(date+"\n"+time).toString()



            val cal:Calendar = Calendar.getInstance();
            val tz:TimeZone = cal.getTimeZone();

            /* debug: is it local time? */
            Log.d("Time zone: ", tz.getDisplayName());

            /* date formatter in local timezone */
            val sdf:SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a");
            sdf.setTimeZone(tz);

            /* print your timestamp and double check it's the date you expect */
            val timestamp = string.toLong()
            finaltime = sdf.format(Date(timestamp)); // I assume your timestamp is in seconds and you're converting to milliseconds?

        }
        catch(e:Exception){
            
        }
        return finaltime

    }

    override fun clickedOnReminder(contest:contestModel) {

        try {
            val stTime=contest.startTime

            val reminderStartTime=Calendar.getInstance()

            Log.d("starttime:",contest.startTime)
            reminderStartTime.set(stTime.slice(6..9).toInt(),stTime.slice(3..4).toInt()-1,stTime.slice(0..1).toInt())


            val intent= Intent(Intent.ACTION_INSERT)
            intent.setData(CalendarContract.Events.CONTENT_URI)
            intent.putExtra(CalendarContract.Events.TITLE,contest.name)
            intent.putExtra(CalendarContract.Events.ALL_DAY,true)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,contest.startTime)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,contest.endTime)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                reminderStartTime.getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                reminderStartTime.getTimeInMillis());




            if(intent.resolveActivity(requireActivity().packageManager)!=null)
            {
                requireActivity().startActivity(intent)
            }
            else
            {
                Toast.makeText(requireActivity(),"There is no App that support this Action",Toast.LENGTH_LONG).show()
            }

        }
        catch (e:Exception)
        {
            Log.e("Exception in opening calender to set reminder",e.toString())
        }


    }

}