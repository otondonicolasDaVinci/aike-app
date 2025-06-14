package com.tesis.aike.data.remote.dto 

import kotlinx.serialization.Serializable 

@Serializable 
data class ChatRequest(
    val prompt: String
)