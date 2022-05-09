package com.vendingmachine.quantityParams

data class Arg(
    val inventory_quantity: Int,
    val location_id: Int,
    val product_id: Int
)