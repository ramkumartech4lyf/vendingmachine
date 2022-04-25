package com.vendingmachine.productsParams

data class Params(
    val context: Context,
    val fields: List<String>,
    val model: String
)