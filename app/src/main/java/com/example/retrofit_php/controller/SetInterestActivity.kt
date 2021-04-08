package com.example.retrofit_php.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.retrofit_php.R
import kotlinx.android.synthetic.main.activity_set_interest.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class SetInterestActivity : AppCompatActivity() {

    lateinit var btnArray : Array<AppCompatButton>
    var interestList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_interest)

        btnArray = arrayOf(
            intertestBtn1,intertestBtn2,intertestBtn3,intertestBtn4,intertestBtn5,
            intertestBtn6,intertestBtn7,intertestBtn8,intertestBtn9,intertestBtn10,
            intertestBtn11,intertestBtn12,intertestBtn13,intertestBtn14,intertestBtn15,
            intertestBtn16)

        setButtonSelected()

        btnSetDoneInterest.setOnClickListener {

            val intent = Intent()
            intent.putStringArrayListExtra("interestList",interestList)
            setResult(RESULT_OK,intent)
            finish()

        }
    }

    private fun setButtonSelected() {
        btnArray.forEach {
            it.setOnClickListener {btn ->
                if(btn.isSelected){
                    val textinterest = it.text.toString()
                    interestList.remove(textinterest)

                    btn.isSelected = false

                }else{
                    val textintertest = it.text.toString()

                    if(interestList.size == 3){
                        Toast.makeText(this,"최대 3개까지 선택할 수 있습니다.",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    interestList.add(textintertest)

                    btn.isSelected = true

                }
            }
        }
    }




}


