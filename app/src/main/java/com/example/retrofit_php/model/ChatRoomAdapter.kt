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
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class ChatRoomAdapter(
    val context: Context,
    private var chatList: MutableList<DataModel.ChatRoomData>,
    val chatRoomName : String,
    val itemClick: (DataModel.ChatRoomData) -> Unit

) : RecyclerView.Adapter<ChatRoomAdapter.ChatViewHolder>() {


    lateinit var preferences: SharedPreferences

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatRoomAdapter.ChatViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_matchchat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ChatViewHolder, position: Int) {

        chatList[position].otherEmail?.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) {

                    return@addSnapshotListener
                }

                if (value.data != null) {
                    val url = value.data!!["imageUri"]

                    Glide.with(context).load(url).apply(RequestOptions().circleCrop())
                        .into(holder.otherImage)


                } else {
                    holder.otherImage.setImageResource(R.drawable.ic_person)
                    return@addSnapshotListener
                }
            }
        }

        holder.otherNickname.text = chatList[position].userNickname

        val room = chatList[position].chatRoomName.toString()

        val last = database.child(room).limitToLast(1)

        val listener = object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                 holder.chatMessage.text = snapshot.child("message").value.toString()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        last.addChildEventListener(listener)
        holder.bind(chatList[position])


    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ChatViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var otherImage: ImageView = itemview.findViewById(R.id.matchImageView)
        var otherNickname: TextView = itemview.findViewById(R.id.matchNickTextView)
        var chatMessage: TextView = itemview.findViewById(R.id.matchChatTextView)

        fun bind(chatData: DataModel.ChatRoomData) {

            itemView.setOnClickListener { itemClick(chatData) }

        }

    }
}