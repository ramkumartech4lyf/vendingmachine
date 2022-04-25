package com.vendingmachine.loginResponse

data class Data(
    val arguments: List<String>,
    val context: Context,
    val debug: String,
    val message: String,
    val name: String
)