package com.dicoding.foundup.data.response

import com.google.gson.annotations.SerializedName

data class UploadCVResponse(

	@field:SerializedName("data")
	val data: DataCV? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class PersonalInfo(

	@field:SerializedName("LinkedIn")
	val linkedIn: String? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("Github")
	val github: String? = null,

	@field:SerializedName("Degree")
	val degree: String? = null,

	@field:SerializedName("Phone Number")
	val phoneNumber: String? = null
)

data class MuhammadErlanggaPrasetya(

	@field:SerializedName("Skills")
	val skills: List<String?>? = null,

	@field:SerializedName("Personal Info")
	val personalInfo: PersonalInfo? = null,

	@field:SerializedName("Work Experience")
	val workExperience: List<WorkExperienceItem?>? = null,

	@field:SerializedName("Certification")
	val certification: List<String?>? = null
)

data class Cv(

	@field:SerializedName("muhammad erlangga prasetya")
	val muhammadErlanggaPrasetya: MuhammadErlanggaPrasetya? = null
)

data class DataCV(

	@field:SerializedName("score")
	val score: Any? = null,

	@field:SerializedName("cv")
	val cv: Cv? = null
)

data class WorkExperienceItem(

	@field:SerializedName("Company Name")
	val companyName: String? = null,

	@field:SerializedName("Start Date")
	val startDate: String? = null,

	@field:SerializedName("Description")
	val description: List<String?>? = null,

	@field:SerializedName("End Date")
	val endDate: String? = null,

	@field:SerializedName("Position")
	val position: String? = null
)
