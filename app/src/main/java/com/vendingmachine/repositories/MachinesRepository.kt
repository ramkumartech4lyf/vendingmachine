package com.vendingmachine.repositories


import com.google.gson.JsonObject
import com.vendingmachine.machinesParams.MachinesParamsHolder
import com.vendingmachine.machinesResponse.MachinesResult
import com.vendingmachine.services.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch



class MachinesRepository {

    private val service: ApiInterface by lazy {
        NetworkManager.baseURL(BaseData.BASE_URL).serviceClass(ApiInterface::class.java)
            .create()
    }
    private val completedJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + completedJob)
    private val foregroundScope = CoroutineScope(Dispatchers.Main)


    fun callMachinesAPI(aHeaderMap: Map<String, String>, machinesParamsHolder: MachinesParamsHolder, taskCallback: TaskCallback<MachinesResult>) {
        backgroundScope.launch {
            when (val result: Result<MachinesResult> =
                service.getMachines(aHeaderMap, machinesParamsHolder).awaitResult()) {
                is Result.Ok -> foregroundScope.launch { taskCallback.onComplete(result.value,  result.response) }
                is Result.Error -> foregroundScope.launch { taskCallback.onException(result.exception) }
                is Result.Exception -> foregroundScope.launch { taskCallback.onException(result.exception) }
            }
        }
    }

}