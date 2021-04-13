package com.example.retrofit_php.navigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit_php.R
import com.example.retrofit_php.controller.ChatActivity
import com.example.retrofit_php.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingFragment : Fragment() {

    private var fragmentView : View? = null
    lateinit var noMatchText : TextView
    lateinit var database : DatabaseReference
    private var iLikeList : MutableList<String> = mutableListOf()
    private var likeMeList : MutableList<String> = mutableListOf()
    private var matchList :  MutableList<String> = mutableListOf()
    lateinit var getuserapi : InterfaceModel.GetUserInfoInterface
    lateinit var preferences: SharedPreferences
    private var chatRoomName = ""

    lateinit var chatroomDTO : DataModel.ChatRoomData
    private var chatroomList: MutableList<DataModel.ChatRoomData> = mutableListOf()
    lateinit var chatAdapter : ChatRoomAdapter

    lateinit var myEmail: String
    private var myNick: String = ""
    lateinit var otherNick : String
    private lateinit var userData: DataModel.UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_match,container,false)

        preferences = context!!.getSharedPreferences("user",Context.MODE_PRIVATE)

//        userData = arguments?.getParcelable("userdata")!!
        myNick = preferences.getString("myNickName","").toString()
        myEmail = preferences.getString("myEmail","").toString()
        Log.e("MF",myEmail)
        database = FirebaseDatabase.getInstance().reference


        getLikeInfo()


        chatAdapter = ChatRoomAdapter(context!!,chatroomList,chatRoomName){
            itemClick(it)
        }
        val recycleMatch : RecyclerView= fragmentView!!.findViewById(R.id.recycler_match)
        recycleMatch.layoutManager = LinearLayoutManager(context)
        recycleMatch.setHasFixedSize(true)
        recycleMatch.adapter = chatAdapter


        return fragmentView


    }

    private fun itemClick(chatData: DataModel.ChatRoomData) {

        val intent = Intent(activity, ChatActivity::class.java)
//        intent.putExtra("myEmail",myEmail)
//        intent.putExtra("myNick",myNick)
        intent.putExtra("otherEmail",chatData.otherEmail)
        intent.putExtra("otherNick",chatData.userNickname)
        startActivity(intent)

    }

    private fun getLikeInfo() {
        Log.e("MF",myEmail)
        val tsDoc = FirebaseFirestore.getInstance().collection("profileImages").document(myEmail)

        tsDoc.get().addOnSuccessListener { doc ->
            if(doc != null){

                val likeMe : HashMap<String,Boolean> = doc.data?.get("likeMe") as HashMap<String, Boolean>
                val ilike : HashMap<String,Boolean> = doc.data?.get("ilike") as HashMap<String, Boolean>

                if(likeMe.isNotEmpty()){
                    for (key in likeMe.keys){
                        likeMeList.add(key)
                    }
                }
                if(ilike.isNotEmpty()){
                    for (key in ilike.keys){
                        iLikeList.add(key)
                    }
                }

                checkMatch()

            }

        }
            .addOnFailureListener {
                Log.d("MathchingFragment","에러 발생")
            }
    }

    private fun checkMatch() {
        likeMeList.forEach { likeme ->
            iLikeList.forEach { ilike ->

                if(likeme == ilike){
                    matchList.add(likeme)
                }
            }
        }
        getOtherNickname()

    }

    private fun getOtherNickname() {


//
//        val mycall: Call<ResponseModel.GetUserDataResponse> = getuserapi.getUserData(myEmail)
//        mycall.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
//            override fun onResponse(
//                call: Call<ResponseModel.GetUserDataResponse>,
//                response: Response<ResponseModel.GetUserDataResponse>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    Log.e("onSuccess1", response.body()!!.toString())
//
//                    val info = response.body()!!
//                    myNick=info.nickname
//
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {
//
//            }
//
//        })

        val retrofit = Repository.getApiClient()
        if(retrofit!=null){
            getuserapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)

        }
        matchList.forEach { otheremail ->
            val othercall: Call<ResponseModel.GetUserDataResponse> = getuserapi.getUserData(otheremail)
            othercall.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
                override fun onResponse(
                    call: Call<ResponseModel.GetUserDataResponse>,
                    response: Response<ResponseModel.GetUserDataResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.e("onSuccess1", response.body()!!.toString())

                        val otherinfo = response.body()!!

                        getChatRoomName(myNick,otherinfo.nickname)
                        chatroomDTO = DataModel.ChatRoomData(chatRoomName,otheremail,otherinfo.nickname)
                        Log.d("room",chatRoomName)
                        chatroomList.add(chatroomDTO)

                        chatAdapter.notifyDataSetChanged()


                    }
                }

                override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

                }

            })
        }


    }

    private fun getChatRoomName(myNick: String, otherNick: String) {
        val nickArray: MutableList<String> = mutableListOf(myNick, otherNick)
        nickArray.sort()
        chatRoomName = "${nickArray[0]}_${nickArray[1]}"
    }

    override fun onResume() {
        chatAdapter.notifyDataSetChanged()
        super.onResume()
    }

}