package com.bagooni.petmliy_android_app.post

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentPostUploadBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PostUploadFragment : Fragment() {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }

    private var _binding: FragmentPostUploadBinding?=null
    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private var contentInput : String = ""
    private var personEmailInput : String = ""


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postImg = binding.postImg
        val glide = Glide.with(activity as MainActivity)

        val acct = GoogleSignIn.getLastSignedInAccount(activity as MainActivity)
        if (acct != null) {
            val personEmail = acct.email
            personEmailInput = personEmail.toString()
            Log.d("google",personEmailInput)
        }

        //앨범 열기
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                imageUri = it.data!!.data
                glide.load(imageUri).centerCrop().into(postImg)
            }
        imagePickerLauncher.launch(
            Intent(Intent.ACTION_PICK).apply {
                this.type = MediaStore.Images.Media.CONTENT_TYPE
            }
        )
        postUpload()
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
            val file = getRealFile(imageUri!!)
            Log.d("log",file.toString())
            val requestFile = file!!.toRequestBody("image/jpg".toMediaType())
            Log.d("log",requestFile.toString())
            val body = MultipartBody.Part.createFormData("postImg", file!!, requestFile)
            Log.d("log", body.toString())
//            val postContent = contentInput.toRequestBody(MultipartBody.FORM)
            val postContent = MultipartBody.Part.createFormData("postContent", contentInput)

            retrofitService.postUpload(personEmailInput,body,postContent).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Log.d("log",response.toString())
                    Log.d("log", response.body().toString())
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("log",t.message.toString())
                    Log.d("log","fail")
                }
            })
            Snackbar.make(requireView(), "포스트 업로드", Snackbar.LENGTH_LONG).show()
            findNavController().navigate(R.id.postFragment)
        }
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