package com.parsanatech.crazycoder.Adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.parsanatech.crazycoder.LetUsChatActivity
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.chatUser

class FriendsChatAdapter():RecyclerView.Adapter<FriendChatViewHolder>() {

    val list=ArrayList<chatUser>()
    var context: Context?=null
    private val MODE_PRIVATE: Int=0

    fun updataData(lst:ArrayList<chatUser>)
    {
        list.clear()
        list.addAll(lst)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendChatViewHolder {

        context=parent.context
        val xmltoview=LayoutInflater.from(parent.context).inflate(R.layout.friend_chat_row,parent,false)

        return FriendChatViewHolder(xmltoview)

    }

    override fun onBindViewHolder(holder: FriendChatViewHolder, position: Int) {

        val currUser=list[position]

        var status:String
        if(currUser.status==false)
        {
            holder.status.setTextColor(Color.parseColor("#5568FE"))
            status="Offline"
        }
        else{
            holder.status.setTextColor(Color.parseColor("#00FF00"))
            status="Online"
        }

        val requestOption=RequestOptions()
        requestOption.placeholder(R.drawable.ic_baseline_account_circle_24)

        holder.name.text=currUser.username
        holder.status.text=status
        Glide.with(holder.itemView.context).setDefaultRequestOptions(requestOption).load(currUser.picture).into(holder.pic)


        holder.itemView.setOnClickListener {

            val intent= Intent(context, LetUsChatActivity::class.java)

            val sharedPref = context!!.getSharedPreferences(
                "com.example.CrazyCoder", this.MODE_PRIVATE)?: return@setOnClickListener
            with(sharedPref.edit())
            {
                putBoolean("Goinning_in_LetUsChat_Activity",true)
                apply()
            }

            intent.putExtra("userId",currUser.id)
            intent.putExtra("profilePic",currUser.picture)
            intent.putExtra("userName",currUser.username)
            context!!.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}

class FriendChatViewHolder(xmlview: View):RecyclerView.ViewHolder(xmlview)
{
    val name=xmlview.findViewById<TextView>(R.id.friendchatname)
    val pic=xmlview.findViewById<ImageView>(R.id.friendchatuserimage)
    val status=xmlview.findViewById<TextView>(R.id.friendchatstatus)
}
