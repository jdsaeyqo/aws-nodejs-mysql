package com.example.retrofit_php.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_php.R
import com.example.retrofit_php.model.Interfaces.GetUserInfoInterface
import com.example.retrofit_php.model.Interfaces.Repository
import com.example.retrofit_php.model.data.GetOtherDataResponse
import com.example.retrofit_php.model.data.GetUserDataResponse
import com.example.retrofit_php.model.data.OtherData
import com.example.retrofit_php.model.data.UserAdapter
import kotlinx.android.synthetic.main.activity_matching.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingActivity : AppCompatActivity() {

    private var useremail: String? = null
    private var otherDataList: MutableList<OtherData> = mutableListOf()

    private val uAdapter = UserAdapter(this, otherDataList) {
        itemClick(it)
    }
    lateinit var getuserapi: GetUserInfoInterface
    lateinit var getmatchapi: GetUserInfoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)

        userRecyclerView.adapter = uAdapter
        val lm = LinearLayoutManager(this)
        userRecyclerView.layoutManager = lm
        userRecyclerView.setHasFixedSize(true)

        getInfo()

    }

    private fun itemClick(otherData: OtherData) {

        val intent = Intent(this, UserDialogActivity::class.java)
        intent.putExtra("email", otherData.email)
        startActivity(intent)

    }

    private fun getInfo() {

        useremail = intent.getStringExtra("useremail")

        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            getuserapi = retrofit.create(GetUserInfoInterface::class.java)
        }
        val call: Call<GetUserDataResponse>? = useremail?.let { getuserapi.getUserData(it) }

        call?.enqueue(object : Callback<GetUserDataResponse> {
            override fun onResponse(
                call: Call<GetUserDataResponse>,
                response: Response<GetUserDataResponse>
            ) {

                if (response.isSuccessful && response.body() != null) {

                    val interest1 = response.body()!!.interest1
                    val interest2 = response.body()!!.interest2
                    val interest3 = response.body()!!.interest3
                        getMatchingUser(interest1, interest2, interest3)

                }
            }

            override fun onFailure(call: Call<GetUserDataResponse>, t: Throwable) {

                t.message?.let { Log.e("onFailure", it) }

            }

        })

    }

    private fun getMatchingUser(interest1: String, interest2: String, interest3: String) {

        val retrofit = Repository.getApiClient()
        if (retrofit != null) {
            getmatchapi = retrofit.create(GetUserInfoInterface::class.java)
        }
        val call: Call<GetOtherDataResponse> = getmatchapi.getMatchingUser(interest1, interest2, interest3)

        call.enqueue(object : Callback<GetOtherDataResponse> {
            override fun onResponse(call: Call<GetOtherDataResponse>, response: Response<GetOtherDataResponse>) {

                if (response.isSuccessful && response.body() != null) {

                    val res = response.body()!!.result

                    for (i in res.indices){
                        if(res[i].email != useremail){
                            val otherdata = OtherData(res[i].email,res[i].nickname)
                            otherDataList.add(otherdata)
                        }


                    }
                    uAdapter.notifyDataSetChanged()


                }

            }

            override fun onFailure(call: Call<GetOtherDataResponse>, t: Throwable) {

                t.message?.let { Log.e("onFailure", it) }

            }


        })


    }

}