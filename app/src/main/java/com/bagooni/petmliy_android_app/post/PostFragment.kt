package com.bagooni.petmliy_android_app.post

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentPostBinding
import com.bagooni.petmliy_android_app.post.Comment.CommentFragment
import com.bagooni.petmliy_android_app.walk.WalkFragment
import com.bagooni.petmliy_android_app.walk.WalkFragment.Companion.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostFragment : Fragment(){
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }
    private var _binding: FragmentPostBinding?=null
    private val binding get() = _binding!!
    lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    //좋아요 구현
    fun postLike(post_id: Int){
        retrofitService.postLike(post_id).enqueue(object:Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

            }
            override fun onFailure(call: Call<Any>, t: Throwable) {

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val feedListView = view.findViewById<RecyclerView>(R.id.feedList)

        binding.uploadButton.setOnClickListener {
            getPermissions()
            findNavController().navigate(R.id.action_postFragment_to_postUploadFragment)
        }
        var gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getPost().enqueue(object : Callback<ArrayList<Post>>{
            override fun onResponse(
                call: Call<ArrayList<Post>>,
                response: Response<ArrayList<Post>>
            ) {
                val postList = response.body()
                val postRecyclerView = view.findViewById<RecyclerView>(R.id.feedList)
                postRecyclerView.adapter = postList?.let {
                    PostRecyclerViewAdapter(
                        it,
                        LayoutInflater.from(activity),
                        Glide.with(activity!!),
                        this@PostFragment,
                        activity as (MainActivity)
                    )
                }
            }
            override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                Log.d("log",t.message.toString())
            }
        })
    }

    class PostRecyclerViewAdapter(
        val postList: ArrayList<Post>,
        val inflater: LayoutInflater,
        val glide: RequestManager,
        val postFragment: PostFragment,
        val activity: MainActivity
    ): RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//            val userImg : ImageView
            val userName : TextView
            val postImg : ImageView
            val postContent : TextView
            val favoriteBtn : ImageButton
            val favoriteColorBtn : ImageButton
            val commentBtn : ImageButton

            init{
//                userImg = itemView.findViewById(R.id.userImg)
                userName = itemView.findViewById(R.id.userName)
                postImg = itemView.findViewById(R.id.postImg)
                postContent = itemView.findViewById(R.id.postContent)
                favoriteBtn = itemView.findViewById(R.id.favoriteBtn) //좋아요 버튼
                favoriteColorBtn = itemView.findViewById(R.id.favoriteColorBtn) //좋아요 색 버튼
                commentBtn = itemView.findViewById(R.id.commentBtn)
                Log.d("log",userName.text.toString())
                Log.d("log",postContent.text.toString())

//                favoriteBtn.setOnClickListener {
//                    postFragment.postLike(postList.get(adapterPosition).postId)
//                    activity.runOnUiThread {
//                        favoriteColorBtn.visibility = VISIBLE
//                    }
//                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.post_recyclerview_item,parent,false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = postList.get(position)

//            post.userImg?.let{
//                glide.load(it).centerCrop().circleCrop().into(holder.petImg)
//            }
            post.postImg.let{
                glide.load(it).centerCrop().into(holder.postImg)
            }
            holder.userName.text = post.email.split("@")[0]
            holder.postContent.text = post.postContent
            holder.commentBtn.setOnClickListener {

            }
        }
        override fun getItemCount(): Int {
            return postList.size
        }
    }
    fun postToComment(){
        findNavController().navigate(R.id.action_postFragment_to_postUploadFragment)
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

    private fun getPermissions(){
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

}
