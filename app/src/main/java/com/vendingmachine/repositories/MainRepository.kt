package com.vendingmachine.repositories

import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.services.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainRepository {

    private val service: ApiInterface by lazy {
        NetworkManager.baseURL(BaseData.BASE_URL).serviceClass(ApiInterface::class.java)
            .create()
    }
    private val completedJob = Job()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + completedJob)
    private val foregroundScope = CoroutineScope(Dispatchers.Main)


    fun callLoginAPI(loginParams: LoginParamsHolder, taskCallback: TaskCallback<LoginResult>) {
        backgroundScope.launch {
            when (val result: Result<LoginResult> =
                service.checkLogin(loginParams).awaitResult()) {
                is Result.Ok -> foregroundScope.launch { taskCallback.onComplete(result.value, result.response) }
                is Result.Error -> foregroundScope.launch { taskCallback.onException(result.exception) }
                is Result.Exception -> foregroundScope.launch { taskCallback.onException(result.exception) }
            }
        }
    }

}