package com.example.retrofit_php.controller

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_php.R
import com.example.retrofit_php.model.ChatAdapter
import com.example.retrofit_php.model.DataModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val chatList: MutableList<DataModel.ChatData> = mutableListOf()
    lateinit var chatAdapter: ChatAdapter
    lateinit var myNick: String
    lateinit var message: String
    lateinit var dateTime: String
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

        getPrefer()
        getIntentData()
        makeRoomName()
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
        otherEmail = intent.getStringExtra("otherEmail").toString()
        otherNick = intent.getStringExtra("otherNick").toString()
        Log.d("otherNick", otherNick)
    }

    private fun getPrefer() {
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        myNick = preferences.getString("myNickName", "").toString()
        myEmail = preferences.getString("myEmail", "").toString()

    }

    private fun openChat() {

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("chatAct", "onAdd : " + snapshot.value)
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
                    message = dto.getValue("message")
                    dateTime = dto.getValue("date_time")

                    val chatdata = DataModel.ChatData(nick, message, dateTime)

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