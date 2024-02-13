package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models

data class ProjectConfig(
    val project_id: Long,
    val app_key: String,
    val url_end_point: String,
    val track: Track,
)

data class Track(
    val auto: Boolean,
    val visit: Boolean,
    val default_properties: Map<String, Any>,
)
