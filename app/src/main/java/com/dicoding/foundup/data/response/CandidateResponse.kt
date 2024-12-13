package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class CandidateResponse(

	@field:SerializedName("data")
	val data: CandidateData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class CandidateUser(

	@field:SerializedName("cv")
	val cv: CandidateCv? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_profile")
	val userProfile: CandidateProfile? = null
)

data class CandidateCv(

	@field:SerializedName("score")
	val score: Any? = null,

	@field:SerializedName("jobRole")
	val jobRole: String? = null
)

data class CandidateData(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("applications")
	val applications: List<ApplicationsItem>
)

data class ApplicationsItem(

	@field:SerializedName("user")
	val user: CandidateUser? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CandidateProfile(

	@field:SerializedName("profilePic")
	val profilePic: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)
