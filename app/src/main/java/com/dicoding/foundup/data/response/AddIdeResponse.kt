package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class AddIdeResponse(

	@field:SerializedName("data")
	val data: AddIdeData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class AddIdeData(

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

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

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("neededRole")
	val neededRole: List<String?>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
