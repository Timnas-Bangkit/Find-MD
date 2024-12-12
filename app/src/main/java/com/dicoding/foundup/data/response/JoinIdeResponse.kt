package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class JoinIdeResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
