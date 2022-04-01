package com.bagooni.petmliy_android_app.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentPostBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostFragment : Fragment(){
    private var _binding: FragmentPostBinding?=null
    private val binding get() = _binding!!
    lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater,container,false)
        binding.uploadButton.setOnClickListener(ButtonListener())
        return binding.root
    }

    inner class ButtonListener: View.OnClickListener{
        override fun onClick(p0: View?){
            when (p0?.id) {
                R.id.uploadButton -> {
                    val intent = Intent(context, PostUploadActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedListView = view.findViewById<RecyclerView>(R.id.feedList)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getPosts().enqueue(object : Callback<ArrayList<Post>>{
            override fun onResponse(
                call: Call<ArrayList<Post>>,
                response: Response<ArrayList<Post>>
            ) {
                val postList = response.body()
                val postRecyclerView = view.findViewById<RecyclerView>(R.id.feedList)
                postRecyclerView.adapter = PostRecyclerViewAdapter(
                    postList!!,
                    LayoutInflater.from(activity),
                    Glide.with(activity!!)
                )
            }

            override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
            }
        })
    }

    class PostRecyclerViewAdapter(
        val postList : ArrayList<Post>,
        val inflater: LayoutInflater,
        val glide: RequestManager
    ): RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val userImg : ImageView
            val userName : TextView
            val postUserName : TextView
            val postImg : ImageView
            val postContent : TextView

            init{
                userImg = itemView.findViewById(R.id.userImg)
                userName = itemView.findViewById(R.id.userName)
                postUserName = itemView.findViewById(R.id.postUserName)
                postImg = itemView.findViewById(R.id.postImg)
                postContent = itemView.findViewById(R.id.postContent)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.post_recyclerview_item,parent,false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = postList.get(position)

            post.owner_profile.image.let {
                glide.load(it).centerCrop().circleCrop().into(holder.userImg)
            }
            post.image.let {
                glide.load(it).centerCrop().into(holder.postImg)
            }
            holder.userName.text = post.owner_profile.username
            holder.postUserName.text = post.owner_profile.username
            holder.postContent.text = post.content
        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }
}
