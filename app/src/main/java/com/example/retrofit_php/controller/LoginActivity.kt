package com.example.retrofit_php.controller

import android.content.Intent
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

    lateinit var  userData : DataModel.UserData
    lateinit var loginapi : InterfaceModel.LoginInterface

    lateinit var name : String
    lateinit var email : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


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

        val email  = editLoginEmail.text.toString()
        val password  = editLoginPass.text.toString()

        val userlogin = DataModel.LoginData(email,password)
        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            loginapi = retrofit.create(InterfaceModel.LoginInterface::class.java)
        }

        val call : Call<ResponseModel.LoginResponse> = loginapi.getUserLogin(userlogin)
        call.enqueue(object : Callback<ResponseModel.LoginResponse> {
            override fun onResponse(call: Call<ResponseModel.LoginResponse>, response: Response<ResponseModel.LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess", response.body()!!.message)
                    Toast.makeText(this@LoginActivity,"로그인 성공", Toast.LENGTH_SHORT).show()
                    val jsonResponse = response.body()
                    Log.e("responseBody", response.body()!!.email)

                    val useremail = response.body()!!.email
                    userData = DataModel.UserData(useremail)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("userdata",userData)

                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ResponseModel.LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

    }


}