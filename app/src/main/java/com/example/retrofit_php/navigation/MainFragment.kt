package com.example.retrofit_php.navigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.retrofit_php.R
import com.example.retrofit_php.controller.MatchingActivity
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    var fragmentView: View? = null
    private lateinit var myEmail: String
    lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_main, container, false)

        userDataUpdate()

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnToMatching.setOnClickListener {

            val intent = Intent(activity, MatchingActivity::class.java)
            startActivity(intent)

        }
    }

    private fun userDataUpdate() {
        preferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        myEmail = preferences.getString("myEmail", "").toString()
        Log.d("userData22", myEmail)
    }


}
