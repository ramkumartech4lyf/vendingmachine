package com.vendingmachine.repositories


import com.vendingmachine.quantityParams.QuantityParamsHolder
import com.vendingmachine.quantityResponse.QuantityResponseHolder
import com.vendingmachine.services.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UpdateRepository {

    private val service: ApiInterface by lazy {
        NetworkManager.baseURL(BaseData.BASE_URL).serviceClass(ApiInterface::class.java)
            .create()
    }
    private val completedJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + completedJob)
    private val foregroundScope = CoroutineScope(Dispatchers.Main)


    fun callUpdateQuantityAPI(aHeaderMap: Map<String, String>, quantityParamsHolder: QuantityParamsHolder, taskCallback: TaskCallback<QuantityResponseHolder>) {
        backgroundScope.launch {
            when (val result: Result<QuantityResponseHolder> =
                service.updateQuantity(aHeaderMap, quantityParamsHolder).awaitResult()) {
                is Result.Ok -> foregroundScope.launch { taskCallback.onComplete(result.value, result.response) }
                is Result.Error -> foregroundScope.launch { taskCallback.onException(result.exception) }
                is Result.Exception -> foregroundScope.launch { taskCallback.onException(result.exception) }
            }
        }
    }

}