package com.vendingmachine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vendingmachine.productResponse.ProductsResult
import com.vendingmachine.productsParams.ProductsParamsHolder
import com.vendingmachine.quantityParams.QuantityParamsHolder
import com.vendingmachine.quantityResponse.QuantityResponseHolder
import com.vendingmachine.repositories.ProductsRepository
import com.vendingmachine.repositories.UpdateRepository
import com.vendingmachine.services.TaskCallback
import okhttp3.Response

class UpdateViewModel : ViewModel(){


    private var mProducts = MutableLiveData<QuantityResponseHolder>()
    val getQuantityResult : LiveData<QuantityResponseHolder> get() = mProducts

    private val updateRepository : UpdateRepository by lazy {
        UpdateRepository()
    }


    val errorObservable = MutableLiveData<Any>()


    fun callUpdateQuantityAPI(aHeaderMap: Map<String, String>, quantityParamsHolder: QuantityParamsHolder) {
        updateRepository.callUpdateQuantityAPI(aHeaderMap, quantityParamsHolder, object :
            TaskCallback<QuantityResponseHolder> {
            override fun onComplete(result: QuantityResponseHolder?, response: Response) {
                mProducts.postValue(result)
            }
            override fun onException(t: Throwable?) {
                errorObservable.postValue(t as Any)
            }
        })
    }
}