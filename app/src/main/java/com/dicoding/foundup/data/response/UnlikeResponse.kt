package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class UnlikeResponse(

	@field:SerializedName("mesage")
	val mesage: String? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)
