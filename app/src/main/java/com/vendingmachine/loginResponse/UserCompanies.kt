package com.vendingmachine.loginResponse

data class UserCompanies(
    val allowed_companies: List<List<Any>>,
    val current_company: List<Any>
)