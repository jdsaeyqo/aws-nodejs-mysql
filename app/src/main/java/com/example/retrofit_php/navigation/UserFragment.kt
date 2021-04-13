package com.example.retrofit_php.navigation


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit_php.R
import com.example.retrofit_php.controller.ILikeDialogActivity
import com.example.retrofit_php.controller.LikeMeDialogActivity
import com.example.retrofit_php.controller.UserInfoUpdate
import com.example.retrofit_php.model.DataModel
import com.example.retrofit_php.model.InterfaceModel
import com.example.retrofit_php.model.Repository
import com.example.retrofit_php.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_set_interest.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserFragment : Fragment() {

    var fragmentView: View? = null
    lateinit var preferences : SharedPreferences
    private lateinit var myNick : String
    lateinit var myEmail : String

    lateinit var nickname: TextView
    lateinit var age: TextView
    lateinit var job: TextView
    lateinit var interest1: TextView
    lateinit var interest2: TextView
    lateinit var interest3: TextView
    private lateinit var userData: DataModel.UserData
    private lateinit var getuserinfoapi: InterfaceModel.GetUserInfoInterface
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

        getPreferences()


        Log.d("UserFrag","Nick: ${preferences.getString("myNickName","")}")
//        userData = arguments?.getParcelable("userdata")!!
//        email = userData.email.toString()

        initView()


        fragmentView?.userProfileImage?.setOnClickListener {
            openGallery()
        }

        getProfileImage()
        getUserInfo()

        fragmentView?.I_like?.setOnClickListener {

            val intent = Intent(activity, ILikeDialogActivity::class.java)
//            intent.putExtra("myemail", email)
            startActivity(intent)

        }
        fragmentView?.like_me?.setOnClickListener {

            val intent = Intent(activity, LikeMeDialogActivity::class.java)
//            intent.putExtra("myemail", email)
            startActivity(intent)

        }

        fragmentView?.updateProfile?.setOnClickListener {

            sendTextAndStartUpdateActivity()

        }

        swipeRefreshLayout = fragmentView!!.findViewById(R.id.swipeLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getProfileImage()
            getUserInfo()
            swipeRefreshLayout.isRefreshing = false
        }

        return fragmentView
    }

    private fun getPreferences() {
        preferences=context!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        myNick = preferences.getString("myNickName","").toString()
        myEmail = preferences.getString("myEmail","").toString()
    }


    private fun sendTextAndStartUpdateActivity() {
        val intent = Intent(activity, UserInfoUpdate::class.java)

//        intent.putExtra("email", userData.email)

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
            getuserinfoapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)
        }

        val call: Call<ResponseModel.GetUserDataResponse> = getuserinfoapi.getUserData(myEmail)
        call.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {
            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess1", response.body()!!.toString())

                    val jsonResponse = response.body()!!
                    userData = DataModel.UserData(
                        myEmail,jsonResponse.nickname,jsonResponse.age, jsonResponse.job,
                        jsonResponse.interest1, jsonResponse.interest2, jsonResponse.interest3
                    )

                    setUserData(userData)

                }
            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

            }

        })


    }

    @SuppressLint("SetTextI18n")
    private fun setUserData(userData: DataModel.UserData) {

        nickname.text = myNick

        if (userData.age != null) {
            age.text = "나이 : ${userData.age}"
        }
        if (userData.job != null) {
            job.text = "직업 : ${userData.job}"
        }
        if (userData.interest1 != "null") {
            interest1.text = userData.interest1
        }
        if (userData.interest2 != "null") {
            interest2.text = userData.interest2
        }
        if (userData.interest3 != "null") {
            interest3.text = userData.interest3
        }

        val tsDoc = FirebaseFirestore.getInstance().collection("profileImages").document(myEmail)

        tsDoc.get().addOnSuccessListener { doc ->

            val ilikeCount = doc.data?.get("ilikeCount").toString()
            val likemeCount = doc.data?.get("likemeCount").toString()

            I_like.text = ilikeCount
            like_me.text = likemeCount

        }
            .addOnFailureListener {
                Log.e("UserFragment", "에러 발생")
            }

        progressBar.visibility = View.GONE

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity?.startActivityForResult(intent, PICK_PROFILE_FROM_ALBUM)

    }

    private fun getProfileImage() {


        myEmail.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, _ ->
                if (value == null) return@addSnapshotListener

                if (value.data != null) {
                    val url = value.data!!["imageUri"] ?: return@addSnapshotListener

                    fragmentView?.let { it1 ->

                        if (activity != null) {
                            Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop())
                                .into(it1.userProfileImage)
                        }

                    }

                } else return@addSnapshotListener
            }
        }

    }

    private fun initView() {

        nickname = fragmentView!!.findViewById(R.id.textNickname)
        age = fragmentView!!.findViewById(R.id.textAge)
        job = fragmentView!!.findViewById(R.id.textJob)
        interest1 = fragmentView!!.findViewById(R.id.textInterest1)
        interest2 = fragmentView!!.findViewById(R.id.textInterest2)
        interest3 = fragmentView!!.findViewById(R.id.textInterest3)
    }

    override fun onStart() {
        getUserInfo()
        super.onStart()
    }

    override fun onResume() {
        Log.d("userfragment", "onResume")
        getUserInfo()
        super.onResume()


    }


}
