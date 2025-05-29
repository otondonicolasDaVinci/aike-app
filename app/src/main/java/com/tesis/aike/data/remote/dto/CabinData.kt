package com.tesis.aike.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CabinData(
    val id: Long? = null,
    val name: String? = null,
    val capacity: Int? = null
)