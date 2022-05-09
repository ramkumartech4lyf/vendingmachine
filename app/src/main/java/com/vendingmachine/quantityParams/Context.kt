package com.vendingmachine.quantityParams

data class Context(
    val active_id: Int,
    val active_ids: List<Int>,
    val active_model: String,
    val allowed_company_ids: List<Int>,
    val default_product_id: Int,
    val inventory_mode: Boolean,
    val lang: String?,
    val tz: String?,
    val uid: Int?
)