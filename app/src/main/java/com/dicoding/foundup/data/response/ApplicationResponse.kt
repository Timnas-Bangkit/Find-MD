package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class ApplicationResponse(

	@field:SerializedName("data")
	val data: ApplicationData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class ApplicationItem(

	@field:SerializedName("post")
	val post: Post? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Post(

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
)

data class ApplicationData(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: Any? = null,

	@field:SerializedName("applications")
	val applications: List<ApplicationItem>
)
