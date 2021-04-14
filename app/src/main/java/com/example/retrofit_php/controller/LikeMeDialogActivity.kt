package com.example.retrofit_php.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_php.R
import com.example.retrofit_php.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_like_me_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikeMeDialogActivity : AppCompatActivity() {

    lateinit var myEmail: String
    private lateinit var getuserinfoapi: InterfaceModel.GetUserInfoInterface
    lateinit var otherData: DataModel.OtherData
    lateinit var otherList: MutableList<DataModel.OtherData>
    private lateinit var mAdapter: UserAdapter
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_me_dialog)

        otherList = mutableListOf()
        mAdapter = UserAdapter(this, otherList) {
            itemClick(it)
        }

        getPrefer()

        recycler_likeme.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        recycler_likeme.layoutManager = lm
        recycler_likeme.setHasFixedSize(true)


        getInfo()

    }

    private fun getPrefer() {
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        myEmail = preferences.getString("myEmail", "").toString()
    }

    private fun getInfo() {

        val tsDoc = FirebaseFirestore.getInstance().collection("profileImages").document(myEmail)


        tsDoc.get().addOnSuccessListener { doc ->
            if (doc != null) {

                val userMap: HashMap<String, Boolean> =
                    doc.data?.get("likeMe") as HashMap<String, Boolean>

                for (key in userMap.keys) {
                    getOtherInfo(key)
                }

                Log.e("list", otherList.toString())

            } else {
                Log.d("likeme", "No Document")
            }

        }.addOnFailureListener {
            Log.d("likeme", "get Failed with", it)
        }


    }

    private fun getOtherInfo(email: String) {

        val retrofit = Repository.getApiClient()


        if (retrofit != null) {
            getuserinfoapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }

        val call: Call<ResponseModel.GetUserDataResponse> = getuserinfoapi.getUserData(email)
        call.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess1", response.body()!!.toString())

                    val result = response.body()!!
                    otherData = DataModel.OtherData(
                        email, result.nickname
                    )
                    Log.e("data", otherData.toString())
                    otherList.add(otherData)
                    mAdapter.notifyDataSetChanged()
                    Log.e("otherList", otherList.toString())
                }
            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

            }

        })

    }


    private fun itemClick(otherData: DataModel.OtherData) {
        val intent = Intent(this, UserDialogActivity::class.java)
        intent.putExtra("myemail", myEmail)
        intent.putExtra("otheremail", otherData.email)
        startActivity(intent)

    }
}