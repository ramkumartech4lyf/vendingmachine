package com.vendingmachine.services

import okhttp3.Response

interface TaskCallback<T> {

    fun onComplete(result: T?, response: Response)
    fun onException(t: Throwable?)

}