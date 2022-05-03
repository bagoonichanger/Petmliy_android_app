package com.bagooni.petmliy_android_app.post

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.bagooni.petmliy_android_app.databinding.ActivityPostUploadBinding
import com.bumptech.glide.Glide
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class PostUploadActivity : AppCompatActivity() {
    var imageUri : Uri? = null
    var contentInput : String = ""
    var imgInput : String = "123"
    var emailInput : String = "yerin506"
    private lateinit var binding: ActivityPostUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPostUploadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener{ finish() }

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
            //val file = getRealFile(imageUri!!)
            //val requestFile = RequestBody.create(
            //    MediaType.parse(contentResolver.getType(imageUri!!).toString())
            //)
            //val postContent = RequestBody.create(MultipartBody.FORM, contentInput)
            //val postImg = RequestBody.create(MultipartBody.FORM, imgInput)
            //val header = HashMap<String, String>()
            //retrofitService.uploadPost(header, content).en

            val data = PostContent(imgInput, contentInput)
            retrofitService.postUpload(data).enqueue(object : Callback<PostContent>{
                override fun onResponse(call: Call<PostContent>, response: Response<PostContent>) {
                    Log.d("log",response.toString())
                    Log.d("log", response.body().toString())
                }

                override fun onFailure(call: Call<PostContent>, t: Throwable) {
                    Log.d("log",t.message.toString())
                    Log.d("log","fail")
                }
            })

        }
    }

    //파일을 얻어오는 법
    private fun getRealFile(uri: Uri): File?{
        var uri: Uri? = uri
        var projection = arrayOf(MediaStore.Images.Media.DATA)
        if(uri == null){
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        //cursor을 이용해서 이미지 가져온 경로를 찾음
        var cursor: Cursor? = contentResolver.query(
            uri!!,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_MODIFIED + "desc"
        )
        if (cursor == null || cursor.columnCount < 1){
            return null
        }
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        if(cursor != null){
            cursor.close()
            cursor = null
        }
        return File(path)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }


}