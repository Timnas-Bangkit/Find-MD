package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class CVProfileResponse(

	@field:SerializedName("data")
	val data: UserData? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class WorkExperiencesItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("companyName")
	val companyName: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("position")
	val position: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UserData(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("cv")
	val cv: DataCVUser? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class DataCVUser(

	@field:SerializedName("skills")
	val skills: List<String?>? = null,

	@field:SerializedName("score")
	val score: Any? = null,

	@field:SerializedName("workExperiences")
	val workExperiences: List<WorkExperiencesItem?>? = null,

	@field:SerializedName("jobRole")
	val jobRole: String? = null,

	@field:SerializedName("certifications")
	val certifications: List<String?>? = null
)
