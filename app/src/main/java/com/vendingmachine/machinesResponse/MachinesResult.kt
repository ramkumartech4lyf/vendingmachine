package com.vendingmachine.machinesResponse

data class MachinesResult(
    val id: Int,
    val jsonrpc: String,
    val result: Result?
)