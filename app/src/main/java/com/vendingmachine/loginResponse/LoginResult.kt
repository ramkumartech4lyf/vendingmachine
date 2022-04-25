package com.vendingmachine.loginResponse

data class LoginResult(
    val id: Any,
    val jsonrpc: String,
    val result: Result?,
    val error: Error?
)