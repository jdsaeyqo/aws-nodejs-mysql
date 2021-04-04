package com.example.retrofit_php.controller


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.retrofit_php.R
import com.example.retrofit_php.model.data.UserData
import com.example.retrofit_php.navigation.MainFragment
import com.example.retrofit_php.navigation.UserFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userDataUpdate()
        bottomNavigationBar.setOnNavigationItemSelectedListener(this)
        bottomNavigationBar.selectedItemId = R.id.action_main

    }

    private fun userDataUpdate() {

        val info = intent.getParcelableExtra<UserData>("userdata")
        if (info != null) {
            userData = UserData(
                email = info.email,
            )
            Log.d("userData",userData.email.toString())
        }



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_main -> {
                val mainFragment = MainFragment()

                val bundle = Bundle()
                bundle.putParcelable("userdata",userData)
                mainFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, mainFragment)
                    .commit()
                return true
            }
            R.id.action_account -> {

                val userFragment = UserFragment()
                val bundle = Bundle()

                bundle.putParcelable("userdata", userData)
                userFragment.arguments = bundle
                Log.d("bundle", bundle.getString("useremail").toString())
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, userFragment)
                    .commit()

                return true
            }
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK){
            return

        }
        when(requestCode){

            UserFragment.PICK_PROFILE_FROM_ALBUM ->{
                val ImageUri : Uri? = data?.data
                val useremail =intent.getParcelableExtra<UserData>("userdata")?.email

                val storageRef =
                    useremail?.let {
                        FirebaseStorage.getInstance().reference.child("profileImages").child(
                            it
                        )
                    }
                if (ImageUri != null) {
                    storageRef?.putFile(ImageUri)?.continueWithTask {
                        return@continueWithTask storageRef.downloadUrl
                    }?.addOnSuccessListener {
                        val map = HashMap<String,Any>()
                        map["image"] = it.toString()
                        FirebaseFirestore.getInstance().collection("profileImages").document(useremail).set(map)

                    }

                }

            }
        }
    }



}

