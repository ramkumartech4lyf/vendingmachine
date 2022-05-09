package com.vendingmachine.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.vendingmachine.R
import com.vendingmachine.adapters.ProductsAdapter
import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.productResponse.Record
import com.vendingmachine.productsParams.ProductsParamsHolder
import com.vendingmachine.viewModels.ProductsViewModel
import java.net.SocketTimeoutException
import java.util.*


class ProductsActivity : AppCompatActivity(), ProductsAdapter.OnProductsClick {

    private lateinit var productsList : RecyclerView
    private lateinit var backArrow : ImageView
    private lateinit var viewModel: ProductsViewModel
    private val mSharedPreferenceMode = 0
    private lateinit var mSharedPreference: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private lateinit var retry: TextView
    private lateinit var title: TextView
    private lateinit var nodata: TextView
    private lateinit var machine : String
    private  var id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        onInitWidget()

        //start action
        viewModel.errorObservable.observe(this) {

            if(it is SocketTimeoutException){
                showErrorDialog("Network error!", "Please check your internet connection!")
            }else{
                showErrorDialog("Unknown error!", "Something went wrong!")
            }

        }

        viewModel.getProducts.observe(this) {

            val products = it.result.records
            val selectedMachineProducts = ArrayList<Record>()

            for(record in products){

                if(record.x_warehouse  != false){

                    Log.d("TAG", "onCreate: eeeeeeeeeeeeee $record")
                    Log.d("TAG", "onCreate: wwwwwwwww $machine")
                    Log.d("TAG", "onCreate: ssssssssss $id")

                    val machineInfo = record.x_warehouse as List<*>
                    val tempId = machineInfo[0].toString().toDouble().toInt()
                    if(tempId == id && machineInfo[1] == machine){
                        selectedMachineProducts.add(record)
                    }
                }
            }

            if(selectedMachineProducts.size <= 0){
                nodata.text = "No products in '$machine'."
                nodata.isVisible = true
                progressVisibleState(false, ListView = false, retryView = false)
            }else{
                setAdapter(selectedMachineProducts)
            }
        }

    }

    private fun onInitWidget(){

        machine = intent.getStringExtra("machine name").toString()
        id = intent.getIntExtra("machine id", 0)
        viewModel = ViewModelProvider(this)[ProductsViewModel::class.java]
        mSharedPreference = this.getSharedPreferences(getString(R.string.app_name), mSharedPreferenceMode)
        productsList = findViewById(R.id.product_list)
        progressBar = findViewById(R.id.progress)
        nodata = findViewById(R.id.no_data)
        title = findViewById(R.id.title)
        title.text = "$machine products"
        retry = findViewById(R.id.retry)
        backArrow = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }
        retry.setOnClickListener {

            progressVisibleState(true, ListView = false, retryView = false)
            setParamsOfMachines()
        }

        setParamsOfMachines()


    }


    private fun setParamsOfMachines(){
        progressVisibleState(true, ListView = false, retryView = false)
        val loginInfoString = mSharedPreference.getString("LoginInfo", null)
        val  loginInfoModel = Gson().fromJson(loginInfoString, LoginResult::class.java)
        val userId = loginInfoModel.result?.user_context?.uid
        val lang = loginInfoModel.result?.user_context?.lang
        val tz = loginInfoModel.result?.user_context?.tz
        val id  =  loginInfoModel.result?.user_companies?.allowed_companies?.get(0)?.get(0)
        val companyIds = listOf(id.toString().toDouble().toInt())

        val context = com.vendingmachine.productsParams.Context(companyIds, lang, tz , userId)

        val fields = listOf("id",
            "product_id",
            "location_id",
            "x_warehouse",
            "quantity",
            "inventory_quantity",
            "product_uom_id",
            "currency_id",
            "value",
            "company_id")

        val params  = com.vendingmachine.productsParams.Params(context,  fields, "stock.quant")

        val productsParamsHolder = ProductsParamsHolder(params)

        Log.d("TAG", "setParamsOfMachines: sssssss $productsParamsHolder")


        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Cookie"] = mSharedPreference.getString("Session ID", "") ?: "No"

        apiCall(headers, productsParamsHolder)
    }

    private fun  apiCall(aHeaderMap: Map<String, String>, productsParamsHolder: ProductsParamsHolder){
        if(checkForInternet()){
            viewModel.callProductsAPI(aHeaderMap, productsParamsHolder)
        }else{
            showErrorDialog("Network error", "Please check your internet connection")
        }
    }

    private fun setAdapter(machines : List<Record>?){

        progressVisibleState(false, ListView = true, retryView = false)
        productsList.adapter = machines?.let { ProductsAdapter(it, this@ProductsActivity) }


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

        progressVisibleState(false, ListView = false, retryView = true)
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun progressVisibleState(progressView : Boolean, ListView: Boolean, retryView : Boolean){
        progressBar.isVisible = progressView
        productsList.isVisible = ListView
        retry.isVisible = retryView
    }

    override fun onProductClick(product: Record) {

        var alert: AlertDialog = AlertDialog.Builder(this).create()
        alert.setTitle(product.product_id[1].toString())
        alert.setButton(Dialog.BUTTON_POSITIVE ,"Update Price") {
            //do your own idea.
                dialog, which ->
//            toUpdateActivity("Update Price", product)
            toUpdateActivity("Update Quantity", product)
        }
        alert.setButton(Dialog.BUTTON_NEUTRAL ," Update Quantity") {
            //do your own idea.
                dialog, which ->

            toUpdateActivity("Update Quantity", product)

        }
        alert.show()



    }

    private fun toUpdateActivity(title : String, product: Record){


        val intent =  Intent(this, PriceUpdateActivity::class.java)
            .putExtra("product id", product.product_id[0].toString().toDouble().toInt())
            .putExtra("product name", product.product_id[1].toString())
            .putExtra("title", title)
            .putExtra("product quantity", product.quantity.toInt())
            .putExtra("id", product.id)
            .putExtra("location id", product.location_id[0].toString().toDouble().toInt())
        resultLauncher.launch(intent)
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setParamsOfMachines()
        }
    }



}