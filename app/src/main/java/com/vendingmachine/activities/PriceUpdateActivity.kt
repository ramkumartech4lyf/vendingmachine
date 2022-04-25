package com.vendingmachine.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vendingmachine.R
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.loginParams.Params

class PriceUpdateActivity : AppCompatActivity() {

    private lateinit var title : TextView
    private lateinit var backArrow : ImageView
    private lateinit var name : TextInputEditText
    private lateinit var price : TextInputEditText
    private lateinit var quantity : TextInputEditText
    private lateinit var nameLay : TextInputLayout
    private lateinit var priceLay : TextInputLayout
    private lateinit var quantityLay : TextInputLayout
    private lateinit var update : Button
    private lateinit var progressDialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_update)

        onInitWidget()
    }

    private fun onInitWidget(){

        title = findViewById(R.id.title)
        backArrow = findViewById(R.id.back_arrow)
        name = findViewById(R.id.name)
        price = findViewById(R.id.price)
        quantity = findViewById(R.id.quantity)
        nameLay = findViewById(R.id.name_layout)
        priceLay = findViewById(R.id.price_layout)
        quantityLay = findViewById(R.id.quantity_layout)
        update = findViewById(R.id.update)

        val oldName = intent.getStringExtra("product name").toString()
        val oldQuantity = intent.getIntExtra("product quantity", 0)

        name.setText(oldName)
        quantity.setText(oldQuantity.toString())
        backArrow.setOnClickListener {
            onBackPressed()
        }

        update.setOnClickListener {
            update()
        }

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                nameLay.isErrorEnabled = false
            }

        })
        price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                priceLay.isErrorEnabled = false
            }

        })

        quantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                quantityLay.isErrorEnabled = false
            }

        })

        progressDialog = AlertDialog.Builder(this).apply {
            setView(R.layout.dialog_progress_layout)
            setCancelable(false)
        }.create()

    }

    private fun update(){
        val tName = name.text.toString().trim()
        val tPrice = price.text.toString().trim()
        val tQuantity = quantity.text.toString().trim()
        if(tName == ""){
            nameLay.error = "Product name is empty!"
            return
        }

        if(tPrice == ""){
            priceLay.error = "Product price is empty!"
            return
        }
        if(tQuantity == ""){
            quantityLay.error = "Product quantity is empty!"
            return
        }
        name.onEditorAction(EditorInfo.IME_ACTION_DONE)
        price.onEditorAction(EditorInfo.IME_ACTION_DONE)
        quantity.onEditorAction(EditorInfo.IME_ACTION_DONE)
        progressDialog.show()

        if(checkForInternet()){


        }else{
            showErrorDialog("Network error", "Please check your internet connection")
        }
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

        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }.show()
    }


}