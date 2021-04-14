package com.example.retrofit_php.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofit_php.R
import com.example.retrofit_php.model.DataModel
import com.example.retrofit_php.model.InterfaceModel
import com.example.retrofit_php.model.Repository
import com.example.retrofit_php.model.ResponseModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var loginapi: InterfaceModel.LoginInterface
    lateinit var preferences: SharedPreferences
    lateinit var getuserapi: InterfaceModel.GetUserInfoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        btnToRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            loginUser()

        }

    }

    private fun loginUser() {

        val email = editLoginEmail.text.toString().trim()
        val password = editLoginPass.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "빈 칸을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val userlogin = DataModel.LoginData(email, password)
        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            loginapi = retrofit.create(InterfaceModel.LoginInterface::class.java)
        }

        val call: Call<ResponseModel.LoginResponse> = loginapi.getUserLogin(userlogin)
        call.enqueue(object : Callback<ResponseModel.LoginResponse> {
            override fun onResponse(
                call: Call<ResponseModel.LoginResponse>,
                response: Response<ResponseModel.LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    Log.e("onSuccess", response.body()!!.message)
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    Log.e("responseBody", response.body()!!.message)


                    val editor = preferences.edit()
                    editor.putString("myEmail", email)
                    editor.apply()

                    getMyNickName(email)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ResponseModel.LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

    }

    private fun getMyNickName(myEmail: String) {
        val retrofit = Repository.getApiClient()
        if (retrofit != null) {
            getuserapi = retrofit.create(InterfaceModel.GetUserInfoInterface::class.java)


        }

        val mycall: Call<ResponseModel.GetUserDataResponse> = getuserapi.getUserData(myEmail)
        mycall.enqueue(object : Callback<ResponseModel.GetUserDataResponse> {

            override fun onResponse(
                call: Call<ResponseModel.GetUserDataResponse>,
                response: Response<ResponseModel.GetUserDataResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess1", response.body()!!.toString())
                    val info = response.body()!!

                    val nickname = info.nickname

                    val editor = preferences.edit()
                    editor.putString("myNickName", nickname)
                    editor.apply()
                    Log.e("LogIn", preferences.getString("myNickName", "").toString())


                }
            }

            override fun onFailure(call: Call<ResponseModel.GetUserDataResponse>, t: Throwable) {

            }

        })


    }


}