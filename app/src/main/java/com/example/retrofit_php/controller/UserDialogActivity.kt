package com.example.retrofit_php.controller


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    lateinit var myEmail: String
    lateinit var otherEmail: String
    lateinit var fireStore: FirebaseFirestore
    lateinit var getuserinfoapi: InterfaceModel.GetUserInfoInterface
    lateinit var userData: DataModel.UserData
    lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdialog)

        getPrefer()
        otherEmail = intent.getStringExtra("otheremail").toString()
        fireStore = FirebaseFirestore.getInstance()

        getProfileImage()
        getUserInfo()

        btnLike.setOnClickListener {
            favoriteEvent()
        }

        btnClose.setOnClickListener {
            finish()
        }
    }

    private fun getPrefer() {
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        myEmail = preferences.getString("myEmail", "").toString()
    }

    private fun favoriteEvent() {

        val tsOtherDoc = fireStore.collection("profileImages").document(otherEmail)
        val tsMyDoc = fireStore.collection("profileImages").document(myEmail)

        fireStore.runTransaction {

            val favoriteOtherData = it.get(tsOtherDoc).toObject(DataModel.FavoriteData::class.java)
            val favoriteMyData = it.get(tsMyDoc).toObject(DataModel.FavoriteData::class.java)

            if (favoriteMyData == null) {
                Toast.makeText(this, "????????? ????????? ?????? ??? ?????????", Toast.LENGTH_SHORT).show()
            }

            if (favoriteOtherData == null) {
                Toast.makeText(this, "????????? ????????? ????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show()

            }

            if (favoriteOtherData != null) {

                //????????? ?????? ??? ????????? ???
                if (!favoriteOtherData.likeMe.contains(myEmail)) {
                    favoriteOtherData.likeMe[myEmail] = true
                    favoriteOtherData.likemeCount++

                    if (favoriteMyData != null) {
                        favoriteMyData.iLike[otherEmail] = true
                        favoriteMyData.iLikeCount++
                    }

                    btnLike.setImageResource(R.drawable.ic_favorite_on)

                    //????????? ?????? ?????? ????????? ???
                } else {
                    favoriteOtherData.likeMe.remove(myEmail)
                    favoriteOtherData.likemeCount--

                    if (favoriteMyData != null) {
                        favoriteMyData.iLike.remove(otherEmail)
                        favoriteMyData.iLikeCount--
                    }

                    btnLike.setImageResource(R.drawable.ic_favorite_off)

                }
                it.set(tsOtherDoc, favoriteOtherData)
                it.set(tsMyDoc, favoriteMyData!!)

            }
        }


    }

    private fun getProfileImage() {
        otherEmail.let {
            fireStore.collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) return@addSnapshotListener

                if (value.data != null) {


                    val url = value.data!!["imageUri"]

                    if(url != null){
                        if (this.isFinishing) return@addSnapshotListener
                        Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                            .into(userProfileImageView)
                    }else{
                        userProfileImageView.setImageResource(R.drawable.ic_person)
                    }


                } else return@addSnapshotListener
            }
        }
    }

    private fun getUserInfo() {
        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            getuserinfoapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }

        val call: Call<ResponseModel.GetUserDataResponse> = getuserinfoapi.getUserData(otherEmail)
        call.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccessDialog", response.body().toString())

                    val jsonResponse = response.body()!!
                    userData = DataModel.UserData(
                        otherEmail, jsonResponse.nickname,
                        jsonResponse.age, jsonResponse.job,
                        jsonResponse.interest1, jsonResponse.interest2, jsonResponse.interest3
                    )

                    bindView(userData)

                }

            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

        val tsDoc = fireStore.collection("profileImages").document(otherEmail)

        fireStore.runTransaction {

            val favoriteData = it.get(tsDoc).toObject(DataModel.FavoriteData::class.java)

            if (favoriteData != null) {
                if (!favoriteData.likeMe.contains(myEmail)) {
                    btnLike.setImageResource(R.drawable.ic_favorite_off)

                } else {
                    btnLike.setImageResource(R.drawable.ic_favorite_on)
                }
                it.set(tsDoc, favoriteData)

            }
        }

    }

    private fun bindView(userData: DataModel.UserData) {

        userNciknameTextView.text = userData.nickname
        if (userNciknameTextView.text == "null") userNciknameTextView.text = "?????? ??????"

        userJobTextView.text = userData.job
        if (userJobTextView.text == "null") userJobTextView.text = "?????? ??????"

        userInterest1TextView.text = userData.interest1
        if (userInterest1TextView.text == "null") userInterest1TextView.text = "?????? ??????"

        userInterest2TextView.text = userData.interest2
        if (userInterest2TextView.text == "null") userInterest2TextView.text = "?????? ??????"

        userInterest3TextView.text = userData.interest3
        if (userInterest3TextView.text == "null") userInterest3TextView.text = "?????? ??????"


    }


}