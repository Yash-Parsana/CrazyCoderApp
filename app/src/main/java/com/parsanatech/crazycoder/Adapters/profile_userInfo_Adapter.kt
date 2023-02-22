package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.profile_userinfo
import kotlinx.coroutines.currentCoroutineContext

class profile_userInfo_Adapter():RecyclerView.Adapter<profile_userInfo_viewHolder>() {

    val userInfoList=ArrayList<profile_userinfo>()
    var context:Context?=null

    fun dataupdated(list:ArrayList<profile_userinfo>)
    {
        userInfoList.clear()
        userInfoList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): profile_userInfo_viewHolder {
        context=parent.context
        val xmltoView=LayoutInflater.from(context).inflate(R.layout.profile_user_info_row,parent,false)

        return profile_userInfo_viewHolder(xmltoView)

    }

    override fun onBindViewHolder(holder: profile_userInfo_viewHolder, position: Int) {
        val currPair=userInfoList.get(position)

        holder.property.text=currPair.property
        holder.value.text=currPair.value

    }

    override fun getItemCount(): Int {
        return userInfoList.size
    }


}
class profile_userInfo_viewHolder(xmlView:View):RecyclerView.ViewHolder(xmlView){
    val property=xmlView.findViewById<TextView>(R.id.profile_property)
    val value=xmlView.findViewById<TextView>(R.id.prifile_property_value)
}

