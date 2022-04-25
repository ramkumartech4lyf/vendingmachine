package com.vendingmachine.productResponse

data class ProductsResult(
    val id: Any,
    val jsonrpc: String,
    val result: Result
)