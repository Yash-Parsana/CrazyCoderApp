package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parsanatech.crazycoder.LetUsChatActivity
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.contestModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class contestInfoAdapter(private val listner:ImplementReminder): RecyclerView.Adapter<ContestViewholder>() {

    val lst=ArrayList<contestModel>()
    var context:Context?=null

    fun dataupdated(list:ArrayList<contestModel>)
    {
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewholder {

        context=parent.context

        val xmlToview=LayoutInflater.from(context).inflate(R.layout.contest_layout,parent,false)

        val finalHolder=ContestViewholder(xmlToview)


        return finalHolder
    }

    override fun onBindViewHolder(holder: ContestViewholder, position: Int) {

        val currcontest=lst.get(position)

        holder.contestname.text=currcontest.name
        holder.startTime.text=currcontest.startTime
        holder.endTime.text=currcontest.endTime


        try {
            holder.reminder.setOnClickListener {

                listner.clickedOnReminder(currcontest)

            }

        }
        catch (e:Exception)
        {
            Log.e("Error in setting clicke listner on reminder",e.toString())
        }



    }

    override fun getItemCount(): Int {

        return lst.size
    }


}

class ContestViewholder(xmlView:View):RecyclerView.ViewHolder(xmlView)
{
    val contestname=xmlView.findViewById<TextView>(R.id.contestname)
    val startTime=xmlView.findViewById<TextView>(R.id.starttime)
    val endTime=xmlView.findViewById<TextView>(R.id.endtime)
    val reminder=xmlView.findViewById<ImageView>(R.id.reminder)
}

interface ImplementReminder{
    fun clickedOnReminder(contestModel: contestModel)
}