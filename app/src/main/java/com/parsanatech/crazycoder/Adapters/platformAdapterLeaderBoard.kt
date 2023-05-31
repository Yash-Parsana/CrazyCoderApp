package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parsanatech.crazycoder.R

class platformAdapterLeaderBoard(val listner:leaderBoardItemSelected,fragment:Int):RecyclerView.Adapter<platformLeaderBoardViewholder>() {

    val platforms=ArrayList<String>()
    val Apiplatforms=ArrayList<String>()
    var context:Context?=null
    val allview=ArrayList<View>()
    var firsttime=true
    var fragmentCall:Int=0 // fragment call specify from which fragment Adapter is called 1:LeaderBoardFragment 2:profileFragment

    init {
        platforms.add("AtCoder")
        platforms.add("CodeChef")
        platforms.add("Codeforces")
//        platforms.add("Interviewbit")
        platforms.add("Leetcode")
//        platforms.add("Spoj")

        Apiplatforms.add("at_coder")
        Apiplatforms.add("code_chef")
        Apiplatforms.add("codeforces")
//        Apiplatforms.add("interviewbit")
        Apiplatforms.add("leet_code")
        fragmentCall=fragment

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): platformLeaderBoardViewholder {

        context=parent.context

        val xmlToview=LayoutInflater.from(context).inflate(R.layout.platforms,parent,false)

        allview.add(xmlToview)

        return platformLeaderBoardViewholder(xmlToview)

    }

    override fun onBindViewHolder(holder: platformLeaderBoardViewholder, position: Int) {

        holder.platformName.text=platforms.get(position)

        if(firsttime&&position==0)
        {
            if(fragmentCall==1)
                holder.itemView.setBackgroundResource(R.drawable.leader_board_selected_bg)
            else
                holder.itemView.setBackgroundResource(R.drawable.profile_platform_selected)

            firsttime=false
        }
        holder.itemView.setOnClickListener {

            allview.forEach {
                it.setBackgroundResource(R.drawable.platform_bg)
            }
            if(fragmentCall==1)
            {
                holder.itemView.setBackgroundResource(R.drawable.leader_board_selected_bg)
            }
            else
            {
                holder.itemView.setBackgroundResource(R.drawable.profile_platform_selected)
            }


            listner.leaderBoardItemSelected(Apiplatforms.get(position))

        }


    }

    override fun getItemCount(): Int {
        return platforms.size
    }


}

class platformLeaderBoardViewholder(xmlView: View):RecyclerView.ViewHolder(xmlView)
{
    val platformName=xmlView.findViewById<TextView>(R.id.platformname)
}
interface leaderBoardItemSelected{

    fun leaderBoardItemSelected(platform:String)

}