package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class AllUserResponse(

	@field:SerializedName("data")
	val data: List<DataItem> = emptyList(),

	@field:SerializedName("message")
	val message: String? = null
)

data class UserProfile(

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class DataItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class User(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: UserProfile? = null
)
