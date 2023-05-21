package com.parsanatech.crazycoder.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parsanatech.crazycoder.R


class PlatformAdapter(private val listner: platFormSelection): RecyclerView.Adapter<platformviewHolder>() {

    val platformList=ArrayList<String>()
    val nameforapi=ArrayList<String>()
    var context:Context?=null
    val allviews=ArrayList<View>()
    var firstTime=true
    var selectedPosition=0

    init
    {
        platformList.add("Atcoder")
        platformList.add("CodeChef")
        platformList.add("Codeforces")
        platformList.add("HackerRank")
        platformList.add("HackerEarth")
//        platformList.add("KikStart")
        platformList.add("LeetCode")


        nameforapi.add("at_coder")
        nameforapi.add("code_chef")
        nameforapi.add("codeforces")
        nameforapi.add("hacker_rank")
        nameforapi.add("hacker_earth")
//        nameforapi.add("kick_start")
        nameforapi.add("leet_code")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): platformviewHolder {

        context=parent.context

        val xmlToView=LayoutInflater.from(context).inflate(R.layout.platforms,parent,false)


        allviews.add(xmlToView)

        return platformviewHolder(xmlToView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: platformviewHolder, position: Int) {
        val platform=platformList.get(position)
        holder.platformName.text=platform

        if(firstTime&&position==0)
        {
            allviews[position].setBackgroundResource(R.drawable.platform_selected_bg)
            firstTime=false
        }

        holder.itemView.setOnClickListener {
            selectedPosition=position
            allviews.forEach {
                it.setBackgroundResource(R.drawable.platform_bg)

            }
            holder.itemView.setBackgroundResource(R.drawable.platform_selected_bg)

            listner.platformselected(nameforapi.get(position))

        }

    }

    override fun getItemCount(): Int {
        return platformList.size
    }

}

class platformviewHolder(xmlview:View):RecyclerView.ViewHolder(xmlview){

    val platformName=xmlview.findViewById<TextView>(R.id.platformname)
}
interface platFormSelection
{
    fun platformselected(platform:String)
}