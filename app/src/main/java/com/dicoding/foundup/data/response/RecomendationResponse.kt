package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class RecomendationResponse(

	@field:SerializedName("data")
	val data: List<RecomendationDataItem>,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class RecomendationUser(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: RecomendationProfile? = null
)

data class RecomendationProfile(

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class RecomendationDataItem(

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

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("user")
	val user: RecomendationUser? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
