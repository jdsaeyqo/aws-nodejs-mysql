package com.example.retrofit_php.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_php.R
import com.example.retrofit_php.model.*
import kotlinx.android.synthetic.main.activity_matching.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingActivity : AppCompatActivity() {

    private var useremail: String? = null
    private var otherDataList: MutableList<DataModel.OtherData> = mutableListOf()

    private val uAdapter = UserAdapter(this, otherDataList) {
        itemClick(it)
    }
    lateinit var getuserapi: InterfaceModel.GetUserInfoInterface
    lateinit var getmatchapi: InterfaceModel.GetUserInfoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)

        userRecyclerView.adapter = uAdapter
        val lm = LinearLayoutManager(this)
        userRecyclerView.layoutManager = lm
        userRecyclerView.setHasFixedSize(true)

        getInfo()

    }

    private fun itemClick(otherData: DataModel.OtherData) {

        val intent = Intent(this, UserDialogActivity::class.java)
        intent.putExtra("email", otherData.email)
        startActivity(intent)

    }

    private fun getInfo() {

        useremail = intent.getStringExtra("useremail")

        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            getuserapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }
        val call: Call<ResponseModel.GetUserDataResponse>? = useremail?.let { getuserapi.getUserData(it) }

        call?.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {

                    val interest1 = response.body()!!.interest1
                    val interest2 = response.body()!!.interest2
                    val interest3 = response.body()!!.interest3
                        getMatchingUser(interest1, interest2, interest3)

                }
            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

                t.message?.let { Log.e("onFailure", it) }

            }

        })

    }

    private fun getMatchingUser(interest1: String, interest2: String, interest3: String) {

        val retrofit = Repository.getApiClient()
        if (retrofit != null) {
            getmatchapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }
        val call: Call<ResponseModel.GetOtherDataResponse> = getmatchapi.getMatchingUser(interest1, interest2, interest3)

        call.enqueue(object : Callback<ResponseModel.GetOtherDataResponse> {
            override fun onResponse(call: Call<ResponseModel.GetOtherDataResponse>, response: Response<ResponseModel.GetOtherDataResponse>) {

                if (response.isSuccessful && response.body() != null) {

                    val res = response.body()!!.result

                    for (i in res.indices){
                        if(res[i].email != useremail){
                            val otherdata = DataModel.OtherData(res[i].email,res[i].nickname)
                            otherDataList.add(otherdata)
                        }


                    }
                    uAdapter.notifyDataSetChanged()


                }

            }

            override fun onFailure(call: Call<ResponseModel.GetOtherDataResponse>, t: Throwable) {

                t.message?.let { Log.e("onFailure", it) }

            }


        })


    }

}