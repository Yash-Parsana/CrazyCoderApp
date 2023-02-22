package com.parsanatech.crazycoder.Adapters

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.models.messageModel

class LetUsChatAdapter:RecyclerView.Adapter<viewholders> {

    var messageLst= ArrayList<messageModel>()
    var context:Context?=null
    var senderId:String?=null
    var receiverId:String?=null
    var firstTime=true
    var funCall=false

    val Date=0
    val SENDER_VIEW_TYPE=1
    val RECEIVER_VIEW_TYPE=2

    constructor(context: Context?, senderId: String?, receiverId: String?) : super() {
        this.context = context
        this.senderId = senderId
        this.receiverId = receiverId
    }


    fun updateData(messageLst: ArrayList<messageModel>)
    {
        funCall=true
        this.messageLst.clear()
        this.messageLst.addAll(messageLst)
        notifyDataSetChanged()
    }



    override fun getItemViewType(position: Int): Int {

        if(messageLst.get(position).senderId=="Date")
        {
            return Date
        }
        if (messageLst.get(position).senderId == FirebaseAuth.getInstance().uid) {
            return SENDER_VIEW_TYPE

        } else return RECEIVER_VIEW_TYPE


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholders {

        if(viewType==Date)
        {
            val xmltoview=LayoutInflater.from(context).inflate(R.layout.show_date_in_chat,parent,false)
            return viewholders.DateViewholer(xmltoview)
        }
        else if(viewType==SENDER_VIEW_TYPE)
        {
            val xmltoview =
                LayoutInflater.from(context).inflate(R.layout.let_us_chat_sender, parent, false)
            return viewholders.senderViewholder(xmltoview)
        }
        else{
            val xmltoview=LayoutInflater.from(context).inflate(R.layout.let_us_chat_receiver,parent,false)
            return viewholders.receiverViewholder(xmltoview)
        }

    }

    override fun onBindViewHolder(holder: viewholders, position: Int) {

        val currMessage=messageLst[position]

        if(holder.itemViewType==Date)
        {
            (holder as viewholders.DateViewholer).date.text=currMessage.message.toString()
        }
        else if(holder.itemViewType==SENDER_VIEW_TYPE)
        {
            (holder as viewholders.senderViewholder).senderMessage.text=currMessage.message.toString()
            (holder as viewholders.senderViewholder).time.text=currMessage.time.toString()
        }
        else{
            (holder as viewholders.receiverViewholder).receiverMessage.text=currMessage.message.toString()
            (holder as viewholders.receiverViewholder).time.text=currMessage.time.toString()
        }

        if(position==messageLst.size-1&&funCall)
        {
            funCall=false
            if(holder.itemViewType==SENDER_VIEW_TYPE&&!firstTime)
            {
                val sound= MediaPlayer.create(context,R.raw.sent)
                sound.start()
            }
            else if(!firstTime)
            {
                val sound= MediaPlayer.create(context,R.raw.receiving)
                sound.start()
            }
            firstTime=false
        }

        holder.itemView.setOnLongClickListener {

            if(holder.itemViewType!=Date)
            {
                if(currMessage.senderId==senderId)
                {
                    val senderChat=senderId+receiverId
                    val receiverChat=receiverId+senderId

                    AlertDialog.Builder(context).setTitle("Delete message?")
                        .setPositiveButton("DELETE FOR ME"){dialogInterface, which ->
                            val db=FirebaseFirestore.getInstance()

                            db.collection("chat").document(senderChat)
                                .collection(senderChat).document(currMessage.id.toString()).delete()

                        }.setNegativeButton("CANCEL"){dialogInterface, which ->

                        }.setNeutralButton("DELETE FOR EVERYONE"){dialogInterface,which->

                            val db=FirebaseFirestore.getInstance()

                            db.collection("chat").document(senderChat).collection(senderChat).document(currMessage.id.toString()).delete().addOnSuccessListener {
                                db.collection("chat").document(receiverChat).collection(receiverChat).document(currMessage.id.toString()).delete()
                            }

                        }.show()
                }
                else{
                    val senderChat=senderId+receiverId

                    AlertDialog.Builder(context).setTitle("Delete message?")
                        .setPositiveButton("DELETE FOR ME"){dialogInterface, which ->
                            val db=FirebaseFirestore.getInstance()

                            db.collection("chat").document(senderChat)
                                .collection(senderChat).document(currMessage.id.toString()).delete()

                        }.setNegativeButton("CANCEL"){dialogInterface, which ->

                        }.show()
                }

            }

            return@setOnLongClickListener true
        }

    }

    override fun getItemCount(): Int {
        return messageLst.size
    }


}

sealed class viewholders(xmlView:View):RecyclerView.ViewHolder(xmlView) {

    class receiverViewholder(rView:View):viewholders(rView)
    {
        val receiverMessage=rView.findViewById<TextView>(R.id.receivermessage)
        val time=rView.findViewById<TextView>(R.id.receivermessagetime)
    }
    class senderViewholder(sView:View):viewholders(sView){

        val senderMessage=sView.findViewById<TextView>(R.id.sendermessage)
        val time=sView.findViewById<TextView>(R.id.sendermessagetime)

    }
    class DateViewholer(dView:View):viewholders(dView)
    {
        val date=dView.findViewById<TextView>(R.id.datevalue)
    }

}

