package com.example.retrofit_php.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.retrofit_php.R
import com.example.retrofit_php.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_i_like_dialog.*
import kotlinx.android.synthetic.main.activity_like_me_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ILikeDialogActivity : AppCompatActivity() {
    lateinit var myEmail : String
    private lateinit var getuserinfoapi : InterfaceModel.GetUserInfoInterface
    lateinit var otherData: DataModel.OtherData
    lateinit var otherList : MutableList<DataModel.OtherData>
    private lateinit var  mAdapter : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_i_like_dialog)

        otherList = mutableListOf()
        mAdapter = UserAdapter(this,otherList){
            itemClick(it)
        }
        myEmail = intent.getStringExtra("myemail").toString()

        recycler_ilike.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        recycler_ilike.layoutManager = lm
        recycler_ilike.setHasFixedSize(true)


        getInfo()
    }

    private fun getInfo() {

        val tsDoc = FirebaseFirestore.getInstance().collection("profileImages").document(myEmail)


        tsDoc.get().addOnSuccessListener { doc ->
            if(doc != null){

                val userMap : HashMap<String,Boolean> = doc.data?.get("ilike") as HashMap<String, Boolean>

                for(key in userMap.keys){
                    getOtherInfo(key)
                }

                Log.e("list",otherList.toString())

            }else{
                Log.d("likeme","No Document")
            }

        }.addOnFailureListener {
            Log.d("likeme","get Failed with",it)
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
                    Log.e("data",otherData.toString())
                    otherList.add(otherData)
                    mAdapter.notifyDataSetChanged()
                    Log.e("otherList",otherList.toString())
                }
            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

            }

        })

    }


    private fun itemClick(otherData: DataModel.OtherData) {
        val intent = Intent(this, UserDialogActivity::class.java)
        intent.putExtra("myemail",myEmail)
        intent.putExtra("otheremail", otherData.email)

        startActivity(intent)

    }


}