package com.dicoding.foundup.ui.uploadIde

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.util.reduceFileImage
import com.dicoding.foundup.data.util.uriToFile
import com.dicoding.foundup.databinding.ActivityUploadIdeBinding
import com.dicoding.foundup.ui.home.HomeFragment
import com.dicoding.foundup.ui.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadIdeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadIdeBinding
    private lateinit var userRepository: UserRepository
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Not get permission!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository =
            UserRepository.getInstance(ApiConfig.getApiService(), UserPreference.getInstance(this))

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.addImage.setOnClickListener {
            openGallery()
        }

        binding.uploadButton.setOnClickListener {
            addStory()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadIdeActivity)
            getFile = myFile

            binding.imgView.setImageURI(selectedImg)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose an Image")
        launcherIntentGallery.launch(chooser)
    }

    private fun addStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart =
                MultipartBody.Part.createFormData("file", file.name, requestImageFile)

            val titleText = binding.titleIdeaEditText.text?.trim().toString()
            val descriptionText = binding.descriptionIdeaEditText.text?.trim().toString()
            val detailText = binding.detailDescriptionEditText.text?.trim().toString()
            val summaryText = binding.summaryEditText.text?.trim().toString()
            val neededRole1Text = binding.neededRole1EditText.text?.trim().toString()
            val neededRole2Text = binding.neededRole2EditText.text?.trim().toString()

            if (titleText.isEmpty() || descriptionText.isEmpty() || detailText.isEmpty() || summaryText.isEmpty() || neededRole1Text.isEmpty() || neededRole2Text.isEmpty()) {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                return
            }

            val titleRequestBody = titleText.toRequestBody("text/plain".toMediaType())
            val descriptionRequestBody = descriptionText.toRequestBody("text/plain".toMediaType())
            val detailRequestBody = detailText.toRequestBody("text/plain".toMediaType())
            val summaryRequestBody = summaryText.toRequestBody("text/plain".toMediaType())
            val neededRole1RequestBody = neededRole1Text.toRequestBody("text/plain".toMediaType())
            val neededRole2RequestBody = neededRole2Text.toRequestBody("text/plain".toMediaType())

            uploadImageToServer(
                imageMultipart,
                titleRequestBody,
                descriptionRequestBody,
                summaryRequestBody,
                detailRequestBody,
                neededRole1RequestBody,
                neededRole2RequestBody
            )
        } else {
            Toast.makeText(this, "Insert an image before uploading!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToServer(
        file: MultipartBody.Part,
        title: RequestBody,
        description: RequestBody,
        summary: RequestBody,
        detail: RequestBody,
        neededRole1: RequestBody,
        neededRole2: RequestBody
    ) {
        lifecycleScope.launch {
            try {
                val token = "Bearer ${userRepository.getUserToken()}"
                val apiService = ApiConfig.getApiService()

                val response = apiService.uploadIde(
                    token, file, title, description, summary, detail, neededRole1, neededRole2
                )

                if (!response.error!!) {
                    showToast("Idea uploaded successfully!")
                    navigateToMain()
                } else {
                    showToast(response.message ?: "Failed to upload idea.")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(HomeFragment.SUCCESS_ADD_IDEA, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
