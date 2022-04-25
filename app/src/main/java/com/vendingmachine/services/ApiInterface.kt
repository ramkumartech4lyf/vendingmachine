package com.vendingmachine.services


import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.machinesParams.MachinesParamsHolder
import com.vendingmachine.machinesResponse.MachinesResult
import com.vendingmachine.productResponse.ProductsResult
import com.vendingmachine.productsParams.ProductsParamsHolder
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("session/authenticate")
    fun checkLogin(@Body loginParams : LoginParamsHolder) : Call<LoginResult>

    @POST("dataset/search_read")
    fun getMachines(@HeaderMap aHeaderMap: Map<String, String>, @Body machinesParamsHolder: MachinesParamsHolder) : Call<MachinesResult>







    @POST("dataset/search_read")
    fun getProducts(@HeaderMap aHeaderMap: Map<String, String>, @Body productsParamsHolder: ProductsParamsHolder) : Call<ProductsResult>

//    @POST("dataset/search_read")
//    fun updatePrice(@Body loginParams : LoginParamsHolder) : Call<LoginResult>

}