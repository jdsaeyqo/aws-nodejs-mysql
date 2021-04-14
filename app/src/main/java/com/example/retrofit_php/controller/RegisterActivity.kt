package com.example.retrofit_php.controller

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.retrofit_php.R
import com.example.retrofit_php.model.DataModel
import com.example.retrofit_php.model.InterfaceModel
import com.example.retrofit_php.model.Repository
import com.example.retrofit_php.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var registerapi: InterfaceModel.RegisterInterface
    lateinit var checkapi: InterfaceModel.CheckNickInterface
    lateinit var preferences: SharedPreferences
    lateinit var myNickName: String
    lateinit var myEmail: String
    private var checkNick = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        checkStoragePermission()
        nicknameTextListener()

        btnCheckNick.setOnClickListener {
            checkUserNick()
        }

        btnRegister.setOnClickListener {
            doRegister()

        }

        btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }


    private fun checkStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
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

            }
        }
    }

    private fun nicknameTextListener() {
        editRegiNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkNick = false
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun showPermissionContextPopup() {

        AlertDialog.Builder(this)
            .setTitle("권한을 요청합니다")
            .setMessage("프로필 사진을 등록하기 위해 권한이 필요합니다")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun checkUserNick() {

        myNickName = editRegiNickName.text.toString().trim()
        if (myNickName.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val check = DataModel.CheckNickData(myNickName)

        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            checkapi = retrofit.create(InterfaceModel.CheckNickInterface::class.java)
        }

        val call: Call<ResponseModel.RegisterResponse> = checkapi.checkUserNick(check)

        call.enqueue(object : Callback<ResponseModel.RegisterResponse> {
            override fun onResponse(
                call: Call<ResponseModel.RegisterResponse>,
                response: Response<ResponseModel.RegisterResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.code == 1) {
                        checkNick = true
                        Log.e("onSuccess", response.body()!!.message)
                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        checkNick = false
                        Log.e("onFailed", response.body()!!.message)
                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<ResponseModel.RegisterResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }

        })

    }

    private fun doRegister() {

        if (!checkNick) {
            Toast.makeText(this, "닉네임 중복확인을 해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        myNickName = editRegiNickName.text.toString().trim()
        myEmail = editEmail.text.toString().trim()
        val password = editPass.text.toString().trim()

        if (myNickName.isEmpty() || myEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "빈 칸을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val registerData = DataModel.RegisterData(myNickName, myEmail, password)


        val retrofit = Repository.getApiClient()

        if (retrofit != null) {
            registerapi = retrofit.create(InterfaceModel.RegisterInterface::class.java)
        }

        val call: Call<ResponseModel.RegisterResponse> = registerapi.getUserRegist(registerData)

        call.enqueue(object : Callback<ResponseModel.RegisterResponse> {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(
                call: Call<ResponseModel.RegisterResponse>,
                response: Response<ResponseModel.RegisterResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    val dto = DataModel.FavoriteData()
                    FirebaseFirestore.getInstance().collection("profileImages").document(myEmail)
                        .set(dto)

                    
                    Log.e("onSuccess", response.body()!!.message)
                    Toast.makeText(
                        this@RegisterActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel.RegisterResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
                Toast.makeText(this@RegisterActivity, "회원가입 에러 발생", Toast.LENGTH_SHORT).show()

            }

        })

    }


}