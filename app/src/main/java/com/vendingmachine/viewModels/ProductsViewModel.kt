package com.vendingmachine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vendingmachine.loginParams.LoginParamsHolder
import com.vendingmachine.productResponse.ProductsResult
import com.vendingmachine.productsParams.ProductsParamsHolder
import com.vendingmachine.repositories.ProductsRepository
import com.vendingmachine.services.TaskCallback
import okhttp3.Response

class ProductsViewModel : ViewModel() {

    private var mProducts = MutableLiveData<ProductsResult>()
    val getProducts : LiveData<ProductsResult> get() = mProducts

    private val productsRepository: ProductsRepository by lazy {
        ProductsRepository()
    }


    val errorObservable = MutableLiveData<Any>()


    fun callProductsAPI(aHeaderMap: Map<String, String>, productsParamsHolder: ProductsParamsHolder) {
        productsRepository.callProductsAPI(aHeaderMap, productsParamsHolder, object : TaskCallback<ProductsResult> {
            override fun onComplete(result: ProductsResult?, response: Response) {
                mProducts.postValue(result)
            }
            override fun onException(t: Throwable?) {
                errorObservable.postValue(t as Any)
            }
        })
    }
}