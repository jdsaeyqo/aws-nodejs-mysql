package com.example.retrofit_php.navigation


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit_php.R
import com.example.retrofit_php.controller.UserInfoUpdate
import com.example.retrofit_php.model.Interfaces.GetUserInfoInterface
import com.example.retrofit_php.model.Interfaces.Repository
import com.example.retrofit_php.model.data.GetUserDataResponse
import com.example.retrofit_php.model.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set_interest.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserFragment : Fragment() {

    var fragmentView: View? = null

    lateinit var email: String
    private lateinit var userData: UserData
    private lateinit var getuserinfoapi: GetUserInfoInterface
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)

        userData = arguments?.getParcelable("userdata")!!
        email = userData.email.toString()


        fragmentView?.userProfileImage?.setOnClickListener {
            openGallery()
        }

        getProfileImage()
        getUserInfo()

        fragmentView?.updateProfile?.setOnClickListener {

            sendTextandStartUpdateAvtivity()

        }

        swipeRefreshLayout = fragmentView!!.findViewById(R.id.swipeLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getUserInfo()
            swipeRefreshLayout.isRefreshing = false
        }

        return fragmentView
    }


    private fun sendTextandStartUpdateAvtivity() {
        val intent = Intent(activity, UserInfoUpdate::class.java)

        intent.putExtra("email", userData.email)

        if (textNickname.text != "") {
            intent.putExtra("nickname", userData.nickname)
        }
        if (textAge.text != "") {
            intent.putExtra("age", userData.age)
        }
        if (textJob.text != "") {
            intent.putExtra("job", userData.job)
        }
        if (textInterest1.text != "") {
            intent.putExtra("interest1", userData.interest1)
        }
        if (textInterest2.text != "") {
            intent.putExtra("interest2", userData.interest2)
        }
        if (textInterest3.text != "") {
            intent.putExtra("interest3", userData.interest3)
        }

        startActivity(intent)
    }

    private fun getUserInfo() {


        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            getuserinfoapi = retrofit.create(GetUserInfoInterface::class.java)
        }

        val call: Call<GetUserDataResponse> = getuserinfoapi.getUserData(email)
        call.enqueue(object : Callback<GetUserDataResponse> {
            override fun onResponse(
                call: Call<GetUserDataResponse>,
                response: Response<GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess1", response.body()!!.toString())

                    val jsonResponse = response.body()!!
                    userData = UserData(
                        email,
                        jsonResponse.nickname, jsonResponse.age, jsonResponse.job,
                        jsonResponse.interest1, jsonResponse.interest2, jsonResponse.interest3
                    )
                    setUserData(userData)

                }
            }

            override fun onFailure(call: Call<GetUserDataResponse>, t: Throwable) {

            }

        })


    }

    private fun setUserData(userData: UserData) {

        if (userData.nickname != "null") {
            textNickname.text = userData.nickname
        }
        if (userData.age != "null") {
            textAge.text = userData.age
        }
        if (userData.job != "null") {
            textJob.text = userData.job
        }
        if (userData.interest1 != "null") {
            textInterest1.text = userData.interest1
        }
        if (userData.interest2 != "null") {
            textInterest2.text = userData.interest2
        }
        if (userData.interest3 != "null") {
            textInterest3.text = userData.interest3
        }

        progressBar.visibility = View.GONE

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity?.startActivityForResult(intent, PICK_PROFILE_FROM_ALBUM)

    }

    private fun getProfileImage() {


        email.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) return@addSnapshotListener

                if (value.data != null) {
                    val url = value.data!!["image"]


                    fragmentView?.let { it1 ->
                        Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                            .into(it1.userProfileImage)
                    }

                } else return@addSnapshotListener
            }
        }


    }

    override fun onStart() {
        Log.d("userfragment", "onStart")
        getUserInfo()
        super.onStart()
    }


    override fun onResume() {
        Log.d("userfragment", "onResume")
        getUserInfo()
        super.onResume()


    }


}
