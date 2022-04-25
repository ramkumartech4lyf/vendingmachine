package com.vendingmachine.loginResponse

import com.google.gson.annotations.SerializedName

data class Result(
    val active_ids_limit: Int,
    val cache_hashes: CacheHashes,
    val company_id: Int,
    val currencies: Currencies,
    val db: String,
    val display_switch_company_menu: Boolean,
    val is_admin: Boolean,
    val is_system: Boolean,
    val max_file_upload_size: Int,
    val max_time_between_keys_in_ms: Int,
    val name: String,
    val notification_type: String,
    val odoobot_initialized: Boolean,
    val partner_display_name: String,
    val partner_id: Int,
    val server_version: String,
    val server_version_info: List<Any>,
    val show_effect: String,
    val uid: Int,
    val user_companies: UserCompanies,
    val user_context: UserContext,
    val user_id: List<Int>,
    val username: String,
    @SerializedName("web.base.url") val web_base_url: String,
    val web_tours: List<Any>
)