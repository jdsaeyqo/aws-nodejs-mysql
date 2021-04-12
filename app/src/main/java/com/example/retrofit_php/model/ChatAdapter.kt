package com.example.retrofit_php.model

import android.content.Context
import android.content.SharedPreferences
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

class ChatAdapter(val context: Context, private var chatList : MutableList<DataModel.ChatData>,
    private val otherEmail:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var preferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.item_mychat,parent,false)
            MyChatViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.item_otherchat,parent,false)
            OtherChatViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is MyChatViewHolder){

            holder.mychatMessage.text = chatList[position].message
            holder.mychatTime.text = chatList[position].date_time
        }
        else if(holder is OtherChatViewHolder){
            otherEmail.let {
                FirebaseFirestore.getInstance().collection("profileImages").document(
                    it
                ).addSnapshotListener { value, error ->
                    if (value == null) {

                        return@addSnapshotListener
                    }

                    if (value.data != null) {
                        val url = value.data!!["imageUri"]

                        Glide.with(context).load(url).apply(RequestOptions().circleCrop())
                            .into(holder.otherProfileImage)


                    } else {
                        holder.otherProfileImage.setImageResource(R.drawable.ic_person)
                        return@addSnapshotListener
                    }
                }
            }
            holder.otherMessage.text = chatList[position].message
            holder.otherChatTime.text = chatList[position].date_time

        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        preferences = context.getSharedPreferences("myNickName",Context.MODE_PRIVATE)

        return if(chatList[position].nickname == preferences.getString("mynick","")){
            1
        }else{
            2
        }


    }

    inner class MyChatViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {
        val mychatMessage:TextView = itemview.findViewById(R.id.myMessageTextView)
        val mychatTime :TextView = itemview.findViewById(R.id.mychattime)
    }

    inner class OtherChatViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {

        val otherProfileImage : ImageView = itemview.findViewById(R.id.otherImage)
        val otherMessage : TextView = itemview.findViewById(R.id.otherChatMessageTextView)
        val otherChatTime : TextView = itemview.findViewById(R.id.otherchattime)

    }
}