package com.vendingmachine.productResponse

data class Record(
    val company_id: List<Any>,
    val currency_id: List<Any>,
    val id: Int,
    val inventory_quantity: Double,
    val location_id: List<Any>,
    val product_id: List<Any>,
    val product_uom_id: List<Any>,
    val quantity: Double,
    val value: Double,
    val x_warehouse: Any
)