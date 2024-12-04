package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class RoleResponse(

	@field:SerializedName("data")
	val data: DataRole? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class DataRole(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
