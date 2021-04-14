package com.example.retrofit_php.controller


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofit_php.R
import com.example.retrofit_php.model.DataModel
import com.example.retrofit_php.navigation.MainFragment
import com.example.retrofit_php.navigation.MatchingFragment
import com.example.retrofit_php.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var myEmail: String
    lateinit var myNickName: String
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        userDataUpdate()
        bottomNavigationBar.setOnNavigationItemSelectedListener(this)
        bottomNavigationBar.selectedItemId = R.id.action_main

    }

    private fun userDataUpdate() {

        myEmail = preferences.getString("myEmail", "").toString()
        myNickName = preferences.getString("myNickName", "").toString()

        Log.d("Main", "myEmail : $myEmail , myNick : $myNickName")

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_main -> {
                val mainFragment = MainFragment()

                supportFragmentManager.beginTransaction().replace(R.id.main_frame, mainFragment)
                    .commit()
                return true
            }
            R.id.action_account -> {

                val userFragment = UserFragment()

                supportFragmentManager.beginTransaction().replace(R.id.main_frame, userFragment)
                    .commit()

                return true
            }
            R.id.action_match -> {
                val matchingFragment = MatchingFragment()

                supportFragmentManager.beginTransaction().replace(R.id.main_frame, matchingFragment)
                    .commit()
                return true
            }
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return

        }
        when (requestCode) {

            UserFragment.PICK_PROFILE_FROM_ALBUM -> {
                val imageUri: Uri? = data?.data


                val storageRef =
                    myEmail.let {
                        FirebaseStorage.getInstance().reference.child("profileImages").child(
                            it
                        )
                    }
                if (imageUri != null) {
                    storageRef.putFile(imageUri).continueWithTask {
                        return@continueWithTask storageRef.downloadUrl
                    }.addOnSuccessListener {

                        val tsDoc = FirebaseFirestore.getInstance().collection("profileImages")
                            .document(myEmail)
                        tsDoc.update("imageUri", it.toString())

                    }

                }

            }
        }
    }


}

