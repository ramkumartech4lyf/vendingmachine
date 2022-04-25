package com.vendingmachine.loginResponse

data class Error(
    val code: Int,
    val `data`: Data,
    val message: String
)