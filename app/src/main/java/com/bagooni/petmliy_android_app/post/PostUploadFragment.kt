package com.bagooni.petmliy_android_app.post


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.LoadingDialog
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentPostUploadBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
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


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PostUploadFragment : Fragment() {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }

    private var _binding: FragmentPostUploadBinding?=null
    private val binding get() = _binding!!
    private var postImageUri : Uri? = null
    private var contentInput : String = ""
    private var personEmailInput : String = ""
    private var userImgUri : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostUploadBinding.inflate(inflater,container,false)
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_postUploadFragment_to_postFragment)
        }
        binding.layout.setOnClickListener { hideKeyboard() }
        return binding.root
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            postImageUri = it.data?.data
            if(it.data == null){
                findNavController().navigate(R.id.action_postUploadFragment_to_postFragment)
            }
            binding.noneImage.visibility = INVISIBLE
            Glide.with(activity as MainActivity).load(postImageUri).centerCrop().into(binding.postImg)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkSign()
        binding.noneImage.setOnClickListener { openGallery() }

        postUpload()
    }

    private fun checkSign(){
        val acct = GoogleSignIn.getLastSignedInAccount(activity as MainActivity)
        if (acct != null) {
            val personEmail = acct.email
            val userImg = acct.photoUrl
            personEmailInput = personEmail.toString()
            userImgUri = userImg.toString()
            Log.d("google",personEmailInput)
            Log.d("google",userImg.toString())
        }
    }

    private fun openGallery(){
        imagePickerLauncher.launch(
            Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            },"사진 선택하기")
        )
    }

    private fun postUpload(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        //입력문구 얻어옴
        binding.postContent.doAfterTextChanged {
            contentInput = it.toString()
        }

        //업로드버튼 클릭
        binding.uploadButton.setOnClickListener{
            val loading = LoadingDialog(activity as MainActivity)
            loading.show()
            val postContent = MultipartBody.Part.createFormData("postContent", contentInput)
            val userUploadFile = MultipartBody.Part.createFormData("userImg",userImgUri)

            val bitmap = postImageUri?.let { it1 -> loadBitmapFromMediaStoreBy(it1) }
            val uploadFile = bitmapToRequestBody("postImg",bitmap)

            if (uploadFile != null) {
                retrofitService.postUpload(personEmailInput, uploadFile, postContent, userUploadFile).enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if(response.isSuccessful){
                            Log.d("log",response.toString())
                        } else{
                            Log.d("error",response.errorBody().toString())
                            Toast.makeText(activity as MainActivity,"동물 사진이 아닙니다.", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.postUploadFragment)
                            loading.dismiss()
                        }
                    }
                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        Toast.makeText(activity as MainActivity,"포스트 업로드했습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.postFragment)
                        loading.dismiss()
                    }
                })
            }
        }
    }

    fun loadBitmapFromMediaStoreBy(photoUri: Uri): Bitmap? {
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
                val changeBitmap =
                    bitmap?.let { Bitmap.createScaledBitmap(it, 400, 400  , false) }
                changeBitmap?.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                bitmap
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            if (imageUri != null) {
                resolver.update(imageUri, imageDetails, null, null)
            }
        }

        Log.d("filename",imageUri.toString()+"_"+imageUri?.encodedPath+"_"+fileName)
        val path = imageUri?.let { getRealFile(it) }
        val file = File(path)
        val file_RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        var uploadFile = MultipartBody.Part.createFormData (name, fileName, file_RequestBody)

        return uploadFile
    }

    //절대경로 string 변환
    private fun getRealFile(uri: Uri): String?{
        var project: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = (activity as MainActivity).contentResolver.query(uri!!,project,null,null,null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        return result
    }

    private fun hideKeyboard() {
        if (activity != null && (activity as MainActivity).currentFocus != null) {
            val inputManager = (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                (activity as MainActivity).currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
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