package com.example.retrofit_php.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit_php.R
import com.example.retrofit_php.model.ChatAdapter
import com.example.retrofit_php.model.DataModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private val chatList: MutableList<DataModel.ChatData> = mutableListOf()
    lateinit var chatAdapter: ChatAdapter
    lateinit var myNick: String
    lateinit var Message: String
    lateinit var DateTime: String
    lateinit var myEmail: String
    lateinit var otherEmail: String
    lateinit var otherNick: String
    lateinit var database: FirebaseDatabase
    lateinit var chatRoomName: String
    lateinit var preferences: SharedPreferences

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        database = FirebaseDatabase.getInstance()


        getIntentData()
        makeRoomName()
        getPrefer()
        openChat()
        initRecyclerView()

        btnSend.setOnClickListener {
            val message = editSendMessage.text.toString()
            val date = Date(System.currentTimeMillis())
            val format = SimpleDateFormat("HH:mm")
            val getTime = format.format(date)

            val chatDTO = DataModel.ChatData(myNick, message, getTime)

            database.reference.child(chatRoomName).push().setValue(chatDTO)
            chatList.add(chatDTO)
            chatAdapter.notifyDataSetChanged()
            chatMessageRecyclerView.scrollToPosition(chatList.size - 1)
            editSendMessage.setText("")


        }

    }

    private fun initRecyclerView() {
        chatAdapter = ChatAdapter(this, chatList, otherEmail)
        chatMessageRecyclerView.adapter = chatAdapter
        val lm = LinearLayoutManager(this)
        chatMessageRecyclerView.layoutManager = lm
        chatMessageRecyclerView.setHasFixedSize(true)
    }

    private fun makeRoomName() {
        val nickArray: MutableList<String> = mutableListOf(myNick, otherNick)
        nickArray.sort()
        chatRoomName = "${nickArray[0]}_${nickArray[1]}"
    }

    private fun getIntentData() {
        myEmail = intent.getStringExtra("myEmail").toString()
        myNick = intent.getStringExtra("myNick").toString()
        otherEmail = intent.getStringExtra("otherEmail").toString()
        otherNick = intent.getStringExtra("otherNick").toString()
        Log.d("otherNick",otherNick)
    }

    private fun getPrefer() {
        preferences = getSharedPreferences("myNickName", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("mynick", myNick)
        editor.apply()
        Log.d("preference", preferences.getString("myNick", "").toString())
    }

    private fun openChat() {

        val listener = object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("chatAct","onAdd : "+snapshot.value)
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
        database.reference.child(chatRoomName).addChildEventListener(listener)

        val listener1 = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children) {
                    val dto = data.value as HashMap<String, String>

                    val nick = dto.getValue("nickname")
                    Message = dto.getValue("message")
                    DateTime = dto.getValue("date_time")

                    val chatdata = DataModel.ChatData(nick, Message, DateTime)

                    chatList.add(chatdata)
                    chatAdapter.notifyDataSetChanged()
                    chatMessageRecyclerView.scrollToPosition(chatList.size - 1)


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        database.reference.child(chatRoomName).addListenerForSingleValueEvent(listener1)


    }
}