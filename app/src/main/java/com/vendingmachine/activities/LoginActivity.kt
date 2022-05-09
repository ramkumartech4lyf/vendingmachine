package com.vendingmachine.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.vendingmachine.R
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.loginParams.Params
import com.vendingmachine.viewModels.MainViewModel
import java.net.SocketTimeoutException

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var username : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var usernameLay : TextInputLayout
    private lateinit var passwordLay : TextInputLayout
    private lateinit var login : Button
    private lateinit var viewModel: MainViewModel
    private lateinit var progressDialog : AlertDialog
    private lateinit var tUsername : String
    private val mSharedPreferenceMode = 0
    private lateinit var mSharedPreference: SharedPreferences
    private lateinit var mEditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onInitWidget()
        setListeners()

        //start action
        viewModel.errorObservable.observe(this) {

            if(it is SocketTimeoutException){
                showErrorDialog("Network error!", "Please check your internet connection!")
            }else{
                showErrorDialog("Unknown error!", "Something went wrong!")
            }

        }

        viewModel.respond.observe(this){

            mEditor.putString("Session ID", it.headers["Set-Cookie"])
            mEditor.commit()
            mEditor.apply()

        }
        viewModel.loginResult.observe(this) {

            if(  it.error == null){

                if(tUsername == it.result?.username){

                    val aGson = Gson()
                    val  tempLoginInfo = aGson.toJson(it)
                    mEditor.putString("LoginInfo", tempLoginInfo)
                    mEditor.commit()
                    mEditor.apply()

                    progressDialog.dismiss()
                    startActivity(Intent(this, MachineActivity::class.java))
                    finish()

                }else{
                    showErrorDialog("Unknown error!", "Something went wrong!")
                }

            }else{


                if(it.error.data.message == "Access Denied"){
                    showErrorDialog("Invalid Info!", "Please enter valid username and password!")
                }else{
                    showErrorDialog("Unknown error!", "Something went wrong!")
                }
            }

        }

    }


    private fun onInitWidget(){

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.log_in)
        usernameLay = findViewById(R.id.username_layout)
        passwordLay= findViewById(R.id.password_layout)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mSharedPreference = this.getSharedPreferences(getString(R.string.app_name), mSharedPreferenceMode)
        mEditor = mSharedPreference.edit()

    }

    private fun setListeners(){
        login.setOnClickListener {


             tUsername = username.text.toString().trim()
            val tPassword = password.text.toString().trim()

            if(tUsername == ""){
                usernameLay.error = "Username is empty!"
                return@setOnClickListener
            }

            if(tPassword == ""){
                passwordLay.error = "Password is empty!"
                return@setOnClickListener
            }
            username.onEditorAction(EditorInfo.IME_ACTION_DONE)
            password.onEditorAction(EditorInfo.IME_ACTION_DONE)
            progressDialog = AlertDialog.Builder(this).apply {
                setView(R.layout.dialog_progress_layout)
                setCancelable(false)
            }.create()

            progressDialog.show()


            if(checkForInternet()){
                viewModel.callLoginAPI(LoginParamsHolder(Params(tUsername, tPassword)))
            }else{
                showErrorDialog("Network error", "Please check your internet connection")
            }




        }


        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                usernameLay.isErrorEnabled = false
            }

        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                passwordLay.isErrorEnabled = false
            }

        })
    }

    private fun checkForInternet(): Boolean {

        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }

    private fun showErrorDialog(title : String, message : String){
        if(progressDialog != null){
            progressDialog.dismiss()
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }.show()
    }


}