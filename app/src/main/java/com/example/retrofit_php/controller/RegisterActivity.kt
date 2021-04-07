package com.example.retrofit_php.controller

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.retrofit_php.R
import com.example.retrofit_php.model.Interfaces.RegisterInterface
import com.example.retrofit_php.model.Interfaces.Repository
import com.example.retrofit_php.model.data.RegiResponse
import com.example.retrofit_php.model.data.RegisterData
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var registerapi: RegisterInterface



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        checkStoragePermission()

        btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            doRegister()

        }


    }
    private fun checkStoragePermission() {
        when{
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                    return

            }

            //교육용 팝업 확인 후 권한 팝업 띄우기
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )

            }            }
    }
    private fun showPermissionContextPopup() {

        AlertDialog.Builder(this)
            .setTitle("권한을 요청합니다")
            .setMessage("프로필 사진을 등록하기 위해 권한이 필요합니다")
            .setPositiveButton("동의하기"){_,_ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
            }
            .setNegativeButton("취소하기"){_,_ -> }
            .create()
            .show()
    }

    private fun doRegister() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()
        val password = editPass.text.toString()

        val registerData = RegisterData(name,email,password)


        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            registerapi = retrofit.create(RegisterInterface::class.java)
        }

        val call: Call<RegiResponse> = registerapi.getUserRegist(registerData)

        call.enqueue(object : Callback<RegiResponse> {
            override fun onResponse(call: Call<RegiResponse>, response: Response<RegiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("onSuccess", response.body()!!.message)

                    Toast.makeText(this@RegisterActivity,"회원가입 성공", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegiResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
                Toast.makeText(this@RegisterActivity,"회원가입 에러 발생", Toast.LENGTH_SHORT).show()

            }

        })

    }


}