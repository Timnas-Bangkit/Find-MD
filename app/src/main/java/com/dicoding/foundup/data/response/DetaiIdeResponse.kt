package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class DetaiIdeResponse(

	@field:SerializedName("data")
	val data: DetaiIdeData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DetaiIdeUser(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: DetaiIdeUserProfile? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DetaiIdeData(

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isLiked")
	val isLiked: Boolean? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("detail")
	val detail: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("user")
	val user: DetaiIdeUser? = null,

	@field:SerializedName("neededRole")
	val neededRole: List<String?>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DetaiIdeUserProfile(

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)
