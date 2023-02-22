package com.parsanatech.crazycoder.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.base.Verify
import com.google.firebase.firestore.FirebaseFirestore
import com.parsanatech.crazycoder.Adapters.sdeAdapter
import com.parsanatech.crazycoder.R
import com.parsanatech.crazycoder.databinding.ActivityAddFriendBinding
import com.parsanatech.crazycoder.databinding.FragmentSdeBinding
import com.parsanatech.crazycoder.models.sdeModel


class sdeFragment : Fragment() {


    lateinit var binding: FragmentSdeBinding
    lateinit var firestore:FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentSdeBinding.inflate(layoutInflater,container,false)

        firestore= FirebaseFirestore.getInstance()

        binding.recyclewview.layoutManager=LinearLayoutManager(context)

        val adapter=sdeAdapter()
        binding.recyclewview.adapter=adapter

        val list=ArrayList<sdeModel>()

        firestore.collection("sdeSheet").get().addOnSuccessListener {

            binding.shhimer.visibility=View.GONE
            binding.recyclewview.visibility=View.VISIBLE

            it.forEach {

                try {
                    list.add(sdeModel(it["name"].toString(),it["topic"].toString(),it["madeby"].toString(),it["experience"].toString(),it["image"].toString(),it["rank"].toString().toInt(),it["link"].toString()))
                }
                catch (e:Exception)
                {
                    Log.e("Excpetion in sde sheet data",e.toString())
                }

            }
            list.sortBy { it.rank }
            adapter.dataChanged(list)


        }

        return binding.root
    }
}