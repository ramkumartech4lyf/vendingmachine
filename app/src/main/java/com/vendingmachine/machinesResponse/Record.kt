package com.vendingmachine.machinesResponse

data class Record(
    val active: Boolean,
    val company_id: List<Any>,
    val id: Int,
    val lot_stock_id: List<Any>,
    val name: String,
    val partner_id: List<Any>,
    val sequence: Int
)