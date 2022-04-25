package com.vendingmachine.repositories

import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.productResponse.ProductsResult
import com.vendingmachine.productsParams.ProductsParamsHolder
import com.vendingmachine.services.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ProductsRepository {

    private val service: ApiInterface by lazy {
        NetworkManager.baseURL(BaseData.BASE_URL).serviceClass(ApiInterface::class.java)
            .create()
    }
    private val completedJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + completedJob)
    private val foregroundScope = CoroutineScope(Dispatchers.Main)


    fun callProductsAPI(aHeaderMap: Map<String, String>, productsParamsHolder: ProductsParamsHolder, taskCallback: TaskCallback<ProductsResult>) {
        backgroundScope.launch {
            when (val result: Result<ProductsResult> =
                service.getProducts(aHeaderMap, productsParamsHolder).awaitResult()) {
                is Result.Ok -> foregroundScope.launch { taskCallback.onComplete(result.value, result.response) }
                is Result.Error -> foregroundScope.launch { taskCallback.onException(result.exception) }
                is Result.Exception -> foregroundScope.launch { taskCallback.onException(result.exception) }
            }
        }
    }

}