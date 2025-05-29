package com.tesis.aike.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservationData(
    val id: Long? = null,
    val user: UserProfileData? = null,
    val cabin: CabinData? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val status: String? = null
)