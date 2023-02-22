package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.LeaderBoardRankers

class LeaderBoardRanklistAdapter(private val listner:FriendRemove):RecyclerView.Adapter<LeaderboardRanklistViewholder>() {

    var context:Context?=null
    val list=ArrayList<LeaderBoardRankers>()

    fun updateData(lst:ArrayList<LeaderBoardRankers>)
    {
        list.clear()
        list.addAll(lst)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardRanklistViewholder {

        context=parent.context
        val xmltoView=LayoutInflater.from(context).inflate(R.layout.leaderboard_ranklist_row,parent,false)


        val finalholder=LeaderboardRanklistViewholder(xmltoView)

        finalholder.deletebtn.setOnClickListener {

                if(list.get(finalholder.adapterPosition).handle!="-")
                {
                    listner.friendremoved(list.get(finalholder.adapterPosition).handle)
                }
        }
        return finalholder
    }

    override fun onBindViewHolder(holder: LeaderboardRanklistViewholder, position: Int) {

        val currRanker=list.get(position)

        if(currRanker.itsMe)
        {
            holder.itemView.setBackgroundResource(R.color.dialogDarkcolor)
        }
        else
        {
            holder.itemView.setBackgroundResource(R.color.darkBG)
        }
        holder.userhandle.text=currRanker.handle
        holder.rank.text=currRanker.rank.toString()

    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class LeaderboardRanklistViewholder(xmlView:View):RecyclerView.ViewHolder(xmlView)
{
    val userhandle=xmlView.findViewById<TextView>(R.id.leaderboarduserhandle)
    val rank=xmlView.findViewById<TextView>(R.id.rank)
    val deletebtn=xmlView.findViewById<ImageView>(R.id.delete)
}
interface FriendRemove{
    fun friendremoved(handle:String)
}