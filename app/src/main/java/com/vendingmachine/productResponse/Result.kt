package com.vendingmachine.productResponse

data class Result(
    val length: Int,
    val records: List<Record>
)