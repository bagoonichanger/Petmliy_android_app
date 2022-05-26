package com.bagooni.petmliy_android_app.home.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bagooni.petmliy_android_app.LoadingDialog
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentResultBinding
import com.bagooni.petmliy_android_app.home.Fragment.Api.AnalysisResult
import com.bagooni.petmliy_android_app.home.Fragment.Api.AnalysisService
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class ResultFragment : Fragment() {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let {
            OkHttpClient.Builder().addInterceptor(it)
                .connectTimeout(100,TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS).build() }

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    lateinit var filePath: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var analysisApi: AnalysisService
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val petImageUri = it.data?.data
            Glide.with(this).load(petImageUri).centerCrop().into(binding.petImg)
            petImageUri?.let { it1 -> getEmotion(it1) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val analyArgs by navArgs<ResultFragmentArgs>()
        val seletNum: Int = analyArgs.selectNum

        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_albumFragment)
        }

        binding.shareButton.setOnClickListener {
            getBitmapFromView(requireView(), requireActivity()) { bitmap ->
                bitmapToUri(bitmap)
            }
        }


        var gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        analysisApi = retrofit.create(AnalysisService::class.java)

        if (seletNum == 1) {
            openCamera()
        } else {
            openGallery()
        }
    }

    private fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                        // possible to handle other result codes ...
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap?) {
        val fileName = "${System.currentTimeMillis()}.jpeg"
        val resolver = requireContext().contentResolver
        val imageCollections =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollections, imageDetails)
        imageUri ?: return

        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap?.let { Bitmap.createScaledBitmap(it, 400, 400  , false) }
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }


        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
        }
        startActivity(Intent.createChooser(sharingIntent, "공유하기"))
    }

    private fun openGallery() {
        imagePickerLauncher.launch(
            Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }, "사진 선택하기")
        )
    }

    private fun openCamera() {
        var petImageUri: Uri? = null
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val option = BitmapFactory.Options()
                    option.inSampleSize = 10
                    val bitmap = BitmapFactory.decodeFile(filePath, option)
                    Glide.with(this).load(petImageUri).centerCrop().into(binding.petImg)
                    petImageUri?.let { it1 -> getEmotion(it1) }
                }
            }
        val filename = "${System.currentTimeMillis()}.jpeg"
        val storageDir: File? =
            (activity as MainActivity).getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val file = File.createTempFile(filename, ".jpeg", storageDir)
        filePath = file.absolutePath

        petImageUri = FileProvider.getUriForFile(
            activity as MainActivity,
            "com.bagooni.petmliy_android_app",
            file
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, petImageUri)
        resultLauncher.launch(intent)
    }

    private fun getEmotion(petImgUri: Uri) {
        val bitmap = petImgUri?.let { it1 -> loadBitmapFromMediaStoreBy(it1) }
        val uploadFile = bitmapToRequestBody("img", bitmap)
        val loading = LoadingDialog(activity as MainActivity)
        loading.show()

        analysisApi.getEmotion(uploadFile).enqueue(object : Callback<AnalysisResult> {
            override fun onResponse(
                call: Call<AnalysisResult>,
                response: Response<AnalysisResult>
            ) {
                if (response.isSuccessful) {
                    Log.d("response",response.body().toString())
                    val result = response.body()
                    result?.let { updateUI(it) }
                    loading.dismiss()
//                    binding.resultConstraintLayout.addView(result?.let {
//                        drawBox(activity as MainActivity, it)
//                    })
                }else{
                    Toast.makeText(activity as MainActivity,"동물 사진이 아닙니다.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.albumFragment)
                    loading.dismiss()
                }
            }
            override fun onFailure(call: Call<AnalysisResult>, t: Throwable) {
                Log.d("onFailure",t.message.toString())
                Toast.makeText(activity as MainActivity,"동물 사진이 아닙니다.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.albumFragment)
                loading.dismiss()
            }
        })
    }

    private class drawBox(context: Context?, val result: AnalysisResult) : View(context) {
        var paint: Paint = Paint()
        val rect = RectF()
        override fun onDraw(canvas: Canvas) {
//            rect.set(
//                result.cropPosition.leftX.toFloat(), result.cropPosition.leftY.toFloat(),
//                (result.cropPosition.rightX - result.cropPosition.leftX).toFloat(),
//                (result.cropPosition.rightY - result.cropPosition.leftY).toFloat())
            rect.set(200f,200f,500f,800f)
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2F
            canvas?.drawRect(rect, paint)
        }

        private fun px2dp(px: Int): Float {
            val resources: Resources = this.resources
            val metrics: DisplayMetrics = resources.displayMetrics
            return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        private fun dp2px(dp: Int): Float {
            val resources = this.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }

    private fun updateUI(result: AnalysisResult){
        binding.petType.text = result.type
        binding.petBreed.text = StringBuilder().append(result.breed.top1)
            .append("(")
            .append(String.format("%.2f", result.breed.top1_result))
            .append("%)")

        var emotionMap = HashMap<String, Float>()
        emotionMap["Angry"] = result.emotion.angry
        emotionMap["Happy"] = result.emotion.happy
        emotionMap["Sad"] = result.emotion.sad

        val maxValue = Collections.max(emotionMap.values)
        emotionMap.entries.stream().forEach { entry ->
            if (entry.value == maxValue) {
                binding.petEmotion.text = StringBuilder().append(entry.key)
                    .append("(").append(entry.value).append(")")
            }
        }

        binding.petEmotionSet.text = StringBuilder()
            .append("Angry(").append(emotionMap["Angry"]).append("%) ")
            .append("Happy(").append(emotionMap["Happy"]).append("%) ")
            .append("Sad(").append(emotionMap["Sad"]).append("%)")
    }

    private fun loadBitmapFromMediaStoreBy(photoUri: Uri): Bitmap? {
        var image: Bitmap? = null
        try {
            image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Api 버전별 이미지 처리
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireActivity().contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    private fun bitmapToRequestBody(name: String, bitmap: Bitmap?): MultipartBody.Part {
        val fileName = "${System.currentTimeMillis()}.jpeg"
        val resolver = requireContext().contentResolver
        val imageCollections =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollections, imageDetails)

        if (imageUri != null) {
            resolver.openOutputStream(imageUri).use { outputStream ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            if (imageUri != null) {
                resolver.update(imageUri, imageDetails, null, null)
            }
        }

        Log.d("filename", imageUri.toString() + "_" + imageUri?.encodedPath + "_" + fileName)
        val path = imageUri?.let { getRealFile(it) }
        val file = File(path)
        val file_RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        var uploadFile = MultipartBody.Part.createFormData(name, fileName, file_RequestBody)

        return uploadFile
    }

    private fun getRealFile(uri: Uri): String? {
        var project: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? =
            (activity as MainActivity).contentResolver.query(uri!!, project, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        return result
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor? {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "MyGitHubData :",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}