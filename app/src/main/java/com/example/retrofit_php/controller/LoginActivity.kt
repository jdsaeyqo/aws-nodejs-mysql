package com.example.retrofit_php.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.retrofit_php.R
import com.example.retrofit_php.model.Interfaces.LoginInterface
import com.example.retrofit_php.model.Interfaces.Repository
import com.example.retrofit_php.model.data.LoginData
import com.example.retrofit_php.model.data.LoginResponse
import com.example.retrofit_php.model.data.UserData
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var  userData : UserData
    lateinit var loginapi : LoginInterface

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

        val userlogin = LoginData(email,password)
        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            loginapi = retrofit.create(LoginInterface::class.java)
        }

        val call : Call<LoginResponse> = loginapi.getUserLogin(userlogin)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess", response.body()!!.message)
                    Toast.makeText(this@LoginActivity,"로그인 성공", Toast.LENGTH_SHORT).show()
                    val jsonResponse = response.body()
                    Log.e("responseBody", response.body()!!.email)

                    val useremail = response.body()!!.email
                    userData = UserData(useremail)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("userdata",userData)

                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

    }


}