package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("data")
	val data: DataUser? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class UserProfiles(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("socialLinks")
	val socialLinks: Any? = null,

	@field:SerializedName("phone")
	val phone: Any? = null,

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("bio")
	val bio: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataUser(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: UserProfiles? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
