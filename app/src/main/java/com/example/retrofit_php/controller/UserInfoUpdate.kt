package com.example.retrofit_php.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.retrofit_php.R
import com.example.retrofit_php.model.Interfaces.Repository
import com.example.retrofit_php.model.Interfaces.UpdateUserInfo
import com.example.retrofit_php.model.data.GetUserDataResponse
import com.example.retrofit_php.model.data.UpdataUserdataResponse
import com.example.retrofit_php.model.data.UserData
import kotlinx.android.synthetic.main.activity_user_info_update.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoUpdate : AppCompatActivity() {

    lateinit var userEmail : String
    lateinit var userdata : UserData

    lateinit var interestTextArray : Array<TextView>

    lateinit var updateapi : UpdateUserInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_update)

        setText()


        btnUpdateDone.setOnClickListener {
            updateUserInfo()
        }

        btnSetInterest.setOnClickListener {
            val intent = Intent(this,SetInterestActivity::class.java)
            startActivityForResult(intent,1001)
        }
    }

    private fun setText() {
      if(intent.getStringExtra("nickname") != null){
          editNickname.setText(intent.getStringExtra("nickname"))

      }
       if(intent.getStringExtra("age") != null){
          editAge.setText(intent.getStringExtra("age"))

      }
       if(intent.getStringExtra("job") != null){
          editJob.setText(intent.getStringExtra("job"))

      }
       if(intent.getStringExtra("interest1") != null){
           editInterest1.text = intent.getStringExtra("interest1")

      }
       if(intent.getStringExtra("interest2") != null){
           editInterest2.text = intent.getStringExtra("interest2")

      }
       if(intent.getStringExtra("interest3") != null){
           editInterest3.text = intent.getStringExtra("interest3")

      }
    }

    private fun setInterestList(interestList: ArrayList<String>) {
        interestTextArray = arrayOf(editInterest1,editInterest2,editInterest3)

        for (i in interestList.indices){
            interestTextArray[i].text = interestList[i]
        }
    }

    private fun updateUserInfo() {

        userEmail = intent.getStringExtra("email").toString()
        val nickname = editNickname.text.toString()
        val age = editAge.text.toString()
        val job = editJob.text.toString()
        val interest1 = editInterest1.text.toString()
        val interest2 = editInterest2.text.toString()
        val interest3 = editInterest3.text.toString()

        userdata = UserData(userEmail,nickname, age, job, interest1, interest2, interest3)

        val retrofit = Repository.getApiClient()

        if(retrofit != null){
            updateapi = retrofit.create(UpdateUserInfo::class.java)

        }

        val call : Call<UpdataUserdataResponse> = updateapi.updateUserInfo(userdata)

        call.enqueue(object : Callback<UpdataUserdataResponse>{

            override fun onResponse(call: Call<UpdataUserdataResponse>, response: Response<UpdataUserdataResponse>) {

                if (response.isSuccessful && response.body() != null){

                    Log.e("onSuccess", response.body()!!.message)


                }
            }
            override fun onFailure(call: Call<UpdataUserdataResponse>, t: Throwable) {

                t.message?.let { Log.e("onFailure", it) }
            }


        })


       finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1001){
            if(resultCode == RESULT_OK){
                if (data != null) {
                    val interestList = data.getStringArrayListExtra("interestList") as ArrayList<String>
                    setInterestList(interestList)
                    Log.d("interestList", interestList[0])
                }
            }
        }
    }
}