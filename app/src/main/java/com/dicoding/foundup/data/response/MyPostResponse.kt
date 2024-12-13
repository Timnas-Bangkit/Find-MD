package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class MyPostResponse(

	@field:SerializedName("data")
	val data: List<MyPostItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class MyPostItem(

	@field:SerializedName("image")
	val image: Any? = null,

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

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("user")
	val user: MyPostUser? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class MyPostUser(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: MyPostProfile? = null
)

data class MyPostProfile(

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)
