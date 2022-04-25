package com.vendingmachine.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vendingmachine.R
import com.vendingmachine.adapters.MachinesAdapter
import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.machinesParams.MachinesParamsHolder
import com.vendingmachine.machinesResponse.Record
import com.vendingmachine.viewModels.MachineViewModel
import java.net.SocketTimeoutException


class MachineActivity : AppCompatActivity(), MachinesAdapter.OnMachinesClick {

    private lateinit var machinesList : RecyclerView
    private val mSharedPreferenceMode = 0
    private lateinit var mSharedPreference: SharedPreferences
    private lateinit var viewModel: MachineViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var retry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine)

        onInitWidget()

        //start action
        viewModel.errorObservable.observe(this) {

            if(it is SocketTimeoutException){
                showErrorDialog("Network error!", "Please check your internet connection!")
            }else{
                showErrorDialog("Unknown error!", "Something went wrong!")
            }

        }

        viewModel.getMachines.observe(this) {

            Log.d("TAG", "onInitWidget: ffffffffffffffffff ${it.result}")

            if(it.result != null){
                progressVisibleState(false, ListView = true, retryView = false)
                setAdapter(it.result.records)
            }else{
                showErrorDialog("Unknown error!", "Something went wrong!")
            }


        }

    }

    private fun onInitWidget(){
        viewModel = ViewModelProvider(this)[MachineViewModel::class.java]
        mSharedPreference = this.getSharedPreferences(getString(R.string.app_name), mSharedPreferenceMode)
        machinesList = findViewById(R.id.machines_list)
        progressBar = findViewById(R.id.progress)
        retry = findViewById(R.id.retry)
        retry.setOnClickListener {

            setParamsOfMachines()
        }

        setParamsOfMachines()

    }

    private fun setParamsOfMachines(){
        val loginInfoString = mSharedPreference.getString("LoginInfo", null)
        val  loginInfoModel = Gson().fromJson(loginInfoString, LoginResult::class.java)
        val userId = loginInfoModel.result?.user_context?.uid
        val lang = loginInfoModel.result?.user_context?.lang
        val tz = loginInfoModel.result?.user_context?.tz
        val id = loginInfoModel.result?.user_companies?.allowed_companies?.get(0)?.get(0)
        val companyIds = listOf(id.toString().toDouble().toInt())



        val context = com.vendingmachine.machinesParams.Context(companyIds, lang, tz , userId)

        val fields = listOf("sequence", "name", "active", "lot_stock_id", "partner_id", "company_id")

        val params  = com.vendingmachine.machinesParams.Params(context,  fields, "stock.warehouse")

        val machinesParamsHolder = MachinesParamsHolder(params)


        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Cookie"] = mSharedPreference.getString("Session ID", "") ?: "No"

        apiCall(headers, machinesParamsHolder)
    }

    private fun  apiCall(aHeaderMap: Map<String, String>, machinesParamsHolder: MachinesParamsHolder){
        progressVisibleState(true, ListView = false, retryView = false)
        if(checkForInternet()){
            viewModel.callMachinesAPI(aHeaderMap, machinesParamsHolder)
        }else{
            showErrorDialog("Network error", "Please check your internet connection")
        }
    }

    private fun setAdapter(machines : List<Record>){

                progressVisibleState(false, ListView = true, retryView = false)
                machinesList.adapter = MachinesAdapter(machines, this@MachineActivity)
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
        machinesList.isVisible = ListView
        retry.isVisible = retryView
    }


    override fun onMachineClick(machine: Record) {

        startActivity(Intent(this, ProductsActivity::class.java)
            .putExtra("machine id", machine.id)
            .putExtra("machine name", machine.name))

    }








}