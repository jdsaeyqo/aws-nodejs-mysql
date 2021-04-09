package com.example.retrofit_php.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit_php.R
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteAdapter(private val context: Context, val itemClick : (DataModel.OtherData) -> Unit) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    var userList : MutableList<DataModel.OtherData> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_userinfo,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.userNickname.text = userList[position].nickname

        userList[position].email.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) {

                    return@addSnapshotListener
                }

                if (value.data != null) {
                    val url = value.data!!["imageUri"]

                    Glide.with(context).load(url).apply(RequestOptions().circleCrop())
                        .into(holder.userImage)


                } else {
                    holder.userImage.setImageResource(R.drawable.ic_person)
                    return@addSnapshotListener
                }
            }
        }
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    inner class MyViewHolder(itemview :View) : RecyclerView.ViewHolder(itemview){

        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userNickname: TextView = itemView.findViewById(R.id.userNickname)

        fun bind(ohterdata : DataModel.OtherData){
            itemView.setOnClickListener { itemClick(ohterdata) }
        }

    }

}