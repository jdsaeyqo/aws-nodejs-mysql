package com.example.retrofit_php.controller


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit_php.R
import com.example.retrofit_php.model.DataModel
import com.example.retrofit_php.model.InterfaceModel
import com.example.retrofit_php.model.Repository
import com.example.retrofit_php.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_userdialog.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDialogActivity : AppCompatActivity() {

    lateinit var useremail: String
    lateinit var getuserinfoapi: InterfaceModel.GetUserInfoInterface
    lateinit var userData: DataModel.UserData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdialog)

        useremail = intent.getStringExtra("email").toString()


        getProfileImage()
        getUserInfo()

        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun getProfileImage() {
        useremail.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) return@addSnapshotListener

                if (value.data != null) {
                    val url = value.data!!["image"]

                    Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                        .into(userProfileImageView)


                } else return@addSnapshotListener
            }
        }
    }

    private fun getUserInfo() {
        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            getuserinfoapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }

        val call: Call<ResponseModel.GetUserDataResponse> = getuserinfoapi.getUserData(useremail)
        call.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccessDialog", response.body().toString())

                    val jsonResponse = response.body()!!
                    userData = DataModel.UserData(
                        useremail,
                        jsonResponse.nickname, jsonResponse.age, jsonResponse.job,
                        jsonResponse.interest1, jsonResponse.interest2, jsonResponse.interest3
                    )

                    bindView(userData)

                }

            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

    }

    private fun bindView(userData: DataModel.UserData) {

        userNciknameTextView.text = userData.nickname
        if (userNciknameTextView.text == "null") userNciknameTextView.text = "정보 없음"

        userJobTextView.text = userData.job
        if (userJobTextView.text == "null") userJobTextView.text = "정보 없음"

        userInterest1TextView.text = userData.interest1
        if (userInterest1TextView.text == "null") userInterest1TextView.text = "정보 없음"

        userInterest2TextView.text = userData.interest2
        if (userInterest2TextView.text == "null") userInterest2TextView.text = "정보 없음"

        userInterest3TextView.text = userData.interest3
        if (userInterest3TextView.text == "null") userInterest3TextView.text = "정보 없음"


    }


}