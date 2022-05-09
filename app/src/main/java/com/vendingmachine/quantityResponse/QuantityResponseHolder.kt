package com.vendingmachine.quantityResponse

data class QuantityResponseHolder(
    val id: Any,
    val jsonrpc: String,
    val result: Int
)