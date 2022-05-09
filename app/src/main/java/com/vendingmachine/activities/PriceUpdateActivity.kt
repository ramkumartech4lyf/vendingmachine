package com.vendingmachine.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.vendingmachine.R
import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.quantityParams.Arg
import com.vendingmachine.quantityParams.Kwargs
import com.vendingmachine.quantityParams.QuantityParamsHolder
import com.vendingmachine.viewModels.UpdateViewModel
import java.net.SocketTimeoutException
import java.util.HashMap

class PriceUpdateActivity : AppCompatActivity() {

    private lateinit var title : TextView
    private lateinit var product_text : TextView
    private lateinit var backArrow : ImageView
    private lateinit var name : TextInputEditText
    private lateinit var price : TextInputEditText
    private lateinit var quantity : TextInputEditText
    private lateinit var nameLay : TextInputLayout
    private lateinit var priceLay : TextInputLayout
    private lateinit var quantityLay : TextInputLayout
    private lateinit var update : Button
    private lateinit var progressDialog : AlertDialog
    private var updateTitle = ""
    private lateinit var viewModel: UpdateViewModel
    private lateinit var mSharedPreference: SharedPreferences
    private val mSharedPreferenceMode = 0
    private var locationId = 0
    private var productId = 0
    private var id = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price_update)

        onInitWidget()


        //start action
        viewModel.errorObservable.observe(this) {

            if(it is SocketTimeoutException){
                showErrorDialog("Network error!", "Please check your internet connection!")
            }else{
                showErrorDialog("Unknown error!", "Something went wrong!")
            }

        }


        viewModel.getQuantityResult.observe(this) {

            if(it != null){

                if(it.result == id){
                    setResult(Activity.RESULT_OK)
                    finish()
                }

            }

        }



    }

    private fun onInitWidget(){
        mSharedPreference = this.getSharedPreferences(getString(R.string.app_name), mSharedPreferenceMode)
        viewModel = ViewModelProvider(this)[UpdateViewModel::class.java]
        title = findViewById(R.id.title)
        product_text = findViewById(R.id.product_name_text)
        backArrow = findViewById(R.id.back_arrow)
        name = findViewById(R.id.name)
        price = findViewById(R.id.price)
        quantity = findViewById(R.id.quantity)
        nameLay = findViewById(R.id.name_layout)
        priceLay = findViewById(R.id.price_layout)
        quantityLay = findViewById(R.id.quantity_layout)
        update = findViewById(R.id.update)

        val oldName = intent.getStringExtra("product name").toString()
        updateTitle = intent.getStringExtra("title").toString()
        val oldQuantity = intent.getIntExtra("product quantity", 0)
        productId = intent.getIntExtra("product id", 0)
        locationId = intent.getIntExtra("location id", 0)
        id = intent.getIntExtra("id", 0)
        title.text = updateTitle

        if(updateTitle == "Update Price"){

            priceLay.visibility = View.VISIBLE
            nameLay.visibility = View.VISIBLE
            quantityLay.visibility = View.GONE
            product_text.visibility = View.GONE

            name.setText(oldName)


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


        }else{


            priceLay.visibility = View.GONE
            nameLay.visibility = View.GONE
            quantityLay.visibility = View.VISIBLE
            product_text.visibility = View.VISIBLE

            product_text.text = oldName
            quantity.setText(oldQuantity.toString())

            quantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    quantityLay.isErrorEnabled = false
                }

            })

        }



        backArrow.setOnClickListener {
            onBackPressed()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        update.setOnClickListener {
            update()
        }





        progressDialog = AlertDialog.Builder(this).apply {
            setView(R.layout.dialog_progress_layout)
            setCancelable(false)
        }.create()

    }

    private fun update(){
        val tName = name.text.toString().trim()
        val tPrice = price.text.toString().trim()
        val tQuantity = quantity.text.toString().trim()


        if(updateTitle == "Update Price"){

            if(tName == ""){
                nameLay.error = "Product name is empty!"
                return
            }

            if(tPrice == ""){
                priceLay.error = "Product price is empty!"
                return
            }


        }else{
            if(tQuantity == ""){
                quantityLay.error = "Product quantity is empty!"
                return
            }
        }


        name.onEditorAction(EditorInfo.IME_ACTION_DONE)
        price.onEditorAction(EditorInfo.IME_ACTION_DONE)
        quantity.onEditorAction(EditorInfo.IME_ACTION_DONE)
        progressDialog.show()

        if(checkForInternet()){
            callQuantityUpdate(tQuantity.toInt())
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
        progressDialog.dismiss()
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun callQuantityUpdate(quantity : Int) {

        val loginInfoString = mSharedPreference.getString("LoginInfo", null)
        val  loginInfoModel = Gson().fromJson(loginInfoString, LoginResult::class.java)

        val userId = loginInfoModel.result?.user_context?.uid
        val lang = loginInfoModel.result?.user_context?.lang
        val tz = loginInfoModel.result?.user_context?.tz
        val id  =  loginInfoModel.result?.user_companies?.allowed_companies?.get(0)?.get(0)
        val companyIds = listOf(id.toString().toDouble().toInt())

        val arg = Arg(quantity,locationId, productId)

        val context = com.vendingmachine.quantityParams.Context(productId, listOf(productId),"product.product",
        companyIds, productId, true, lang, tz, userId)
        val kwargs = Kwargs(context)
        val params = com.vendingmachine.quantityParams.Params(listOf(arg),kwargs,"create","stock.quant")


        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Cookie"] = mSharedPreference.getString("Session ID", "") ?: "No"
        viewModel.callUpdateQuantityAPI(headers, QuantityParamsHolder(params))



    }

}

