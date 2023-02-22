package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.sdeModel

class sdeAdapter:RecyclerView.Adapter<sdeViewholder>() {

    var context:Context?=null
    val list=ArrayList<sdeModel>()

    fun dataChanged(lst:ArrayList<sdeModel>)
    {
        list.clear()
        list.addAll(lst)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sdeViewholder {

        context=parent.context
        val xmltoView=LayoutInflater.from(context).inflate(R.layout.sde_sheet_row,parent,false)

        return sdeViewholder(xmltoView)
    }

    override fun onBindViewHolder(holder: sdeViewholder, position: Int) {

        val obj=list[position]

        holder.sheetname.text=obj.sheetname
        holder.topics.text=obj.topic
        holder.madeby.text=obj.madeby
        holder.workedAt.text=obj.workedat


        if(obj.image=="striver"||obj.image=="strivercp")
        {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.striver)
            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOption).load(R.drawable.striver).into(holder.image)
        }
        else if(obj.image=="shrdha")
        {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.shardhadidi)
            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOption).load(R.drawable.shardhadidi).into(holder.image)
        }
        else if(obj.image=="fraz")
        {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.fraz)
            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOption).load(R.drawable.fraz).into(holder.image)
        }
        else if(obj.image=="love")
        {
            val requestOption= RequestOptions()
            requestOption.placeholder(R.drawable.lovebabber)
            Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOption).load(R.drawable.lovebabber).into(holder.image)
        }


        holder.itemView.setOnClickListener {

            if(obj.image=="striver")
            {
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(obj.link))
                context!!.startActivity(intent)
            }
            else if(obj.image=="strivercp")
            {
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(obj.link))
                context!!.startActivity(intent)
            }
            else if(obj.image=="love")
            {
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(obj.link))
                context!!.startActivity(intent)
            }
            else if(obj.image=="fraz")
            {
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(obj.link))
                context!!.startActivity(intent)
            }
            else if(obj.image=="shrdha")
            {
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(obj.link))
                context!!.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class sdeViewholder(xmlview:View):RecyclerView.ViewHolder(xmlview)
{
    val sheetname=xmlview.findViewById<TextView>(R.id.sheetname)
    val topics=xmlview.findViewById<TextView>(R.id.topics)
    val madeby=xmlview.findViewById<TextView>(R.id.madeby)
    val workedAt=xmlview.findViewById<TextView>(R.id.workedat)
    val image=xmlview.findViewById<ImageView>(R.id.sdesheetImage)
}