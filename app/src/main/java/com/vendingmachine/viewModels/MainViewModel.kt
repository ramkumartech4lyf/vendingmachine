package com.vendingmachine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vendingmachine.loginResponse.LoginResult
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.repositories.MainRepository
import com.vendingmachine.services.TaskCallback
import okhttp3.Response

class MainViewModel : ViewModel() {


    private var mLoginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = mLoginResult

    private var mRespond = MutableLiveData<Response>()
    val respond: LiveData<Response> get() = mRespond

    private val mainRepository: MainRepository by lazy {
        MainRepository()
    }

    val errorObservable = MutableLiveData<Any>()


    fun callLoginAPI(loginParams: LoginParamsHolder) {
        mainRepository.callLoginAPI(loginParams, object : TaskCallback<LoginResult> {
            override fun onComplete(result: LoginResult?, response: Response) {
                mLoginResult.postValue(result)
                mRespond.postValue(response)
            }
            override fun onException(t: Throwable?) {
                errorObservable.postValue(t as Any)
            }
        })
    }


}