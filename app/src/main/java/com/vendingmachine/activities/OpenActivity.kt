package com.vendingmachine.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.vendingmachine.R
import com.vendingmachine.loginResponse.LoginResult
import java.util.*
import kotlin.concurrent.schedule

class OpenActivity : AppCompatActivity() {


    private lateinit var mSharedPreference: SharedPreferences
    private val mSharedPreferenceMode = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        mSharedPreference = this.getSharedPreferences(getString(R.string.app_name), mSharedPreferenceMode)
        val loginInfoString = mSharedPreference.getString("LoginInfo", null)
        val  loginInfoModel  = Gson().fromJson(loginInfoString, LoginResult::class.java)

        var username : String? = null

        if(loginInfoModel != null){
            username = loginInfoModel.result?.username
        }

        Timer().schedule(3000){
            if(username != null){
                startActivity(Intent(this@OpenActivity, MachineActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@OpenActivity, MainActivity::class.java))
                finish()
            }
        }





    }
}