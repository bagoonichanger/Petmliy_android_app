package com.bagooni.petmliy_android_app.post

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PostUploadFragment : Fragment() {
    private var _binding: FragmentPostUploadBinding?=null
    private val binding get() = _binding!!

    var imageUri : Uri? = null
    var contentInput : String = ""
    var imgInput : String = "123"
    var emailInput : String = "yerin506"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostUploadBinding.inflate(inflater,container,false)
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_postUploadFragment_to_postFragment) }
        binding.layout.setOnClickListener { hideKeyboard() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postImg = binding.postImg
        val glide = Glide.with(this)

        //앨범열기
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
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file!!)

            val body = MultipartBody.Part.createFormData("image", file!!, requestFile)
            val postContent = RequestBody.create(MultipartBody.FORM, contentInput)
            val header = HashMap<String, String>()
            val sp = (activity as MainActivity).getSharedPreferences(
                "user_info", Context.MODE_PRIVATE)
            val token = sp.getString("token","")
            header.put("Authorization",token!!)

            retrofitService.uploadPost(header,body,postContent).enqueue(object : Callback<Any>{
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
            //val postImg = RequestBody.create(MultipartBody.FORM, imgInput)
            //val data = PostContent(imgInput, contentInput)
            //retrofitService.postUpload(data).enqueue(object : Callback<PostContent> {
            //    override fun onResponse(call: Call<PostContent>, response: Response<PostContent>) {
            //        Log.d("log",response.toString())
            //        Log.d("log", response.body().toString())
            //    }

            //    override fun onFailure(call: Call<PostContent>, t: Throwable) {
            //        Log.d("log",t.message.toString())
            //        Log.d("log","fail")
            //    }
            //})
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


}