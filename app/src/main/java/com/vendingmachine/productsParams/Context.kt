package com.vendingmachine.productsParams

data class Context(
    val allowed_company_ids: List<Int>,
    val lang: String?,
    val tz: String?,
    val uid: Int?
)