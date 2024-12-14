//import android.content.Context
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.dicoding.foundup.data.UserRepository
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import java.io.File
//
//class UploadCVWorker(
//    context: Context,
//    workerParams: WorkerParameters,
//    private val userRepository: UserRepository // Buat ini injeksi manual jika perlu
//) : Worker(context, workerParams) {
//
//    override fun doWork(): Result {
//        return try {
//            val token = inputData.getString("TOKEN") ?: return Result.failure()
//            val filePath = inputData.getString("FILE_PATH") ?: return Result.failure()
//
//            val file = File(filePath)
//            val requestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
//            val filePart = MultipartBody.Part.createFormData("cv", file.name, requestBody)
//
//            val response = userRepository.uploadCV(token, filePart)
//
//            if (response.error == false) {
//                Result.success()
//            } else {
//                Result.failure()
//            }
//        } catch (e: Exception) {
//            Result.failure()
//        }
//    }
//}
