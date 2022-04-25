package com.vendingmachine.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.vendingmachine.machinesParams.MachinesParamsHolder
import com.vendingmachine.machinesResponse.MachinesResult
import com.vendingmachine.repositories.MachinesRepository
import com.vendingmachine.services.TaskCallback
import okhttp3.Response


class MachineViewModel : ViewModel() {

    private var mMachines = MutableLiveData<MachinesResult>()
    val getMachines : LiveData<MachinesResult> get() = mMachines

    private val machineRepository: MachinesRepository by lazy {
        MachinesRepository()
    }


    val errorObservable = MutableLiveData<Any>()


    fun callMachinesAPI(aHeaderMap: Map<String, String>,machinesParamsHolder: MachinesParamsHolder) {
        machineRepository.callMachinesAPI(aHeaderMap, machinesParamsHolder, object : TaskCallback<MachinesResult> {
            override fun onComplete(result: MachinesResult?, response: Response) {
                mMachines.postValue(result)
            }
            override fun onException(t: Throwable?) {
                errorObservable.postValue(t as Any)
            }
        })
    }

}