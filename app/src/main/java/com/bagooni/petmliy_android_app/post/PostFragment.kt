package com.bagooni.petmliy_android_app.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ActionProvider
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.MainActivity
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

    //좋아요 구현
    fun postLike(post_id: Int){
        retrofitService.postLike(post_id).enqueue(object:Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {

            }
        })
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

        retrofitService.getInstaPosts().enqueue(object : Callback<ArrayList<InstaPost>>{
            override fun onResponse(
                call: Call<ArrayList<InstaPost>>,
                response: Response<ArrayList<InstaPost>>
            ) {
                val postList = response.body()
                val postRecyclerView = view.findViewById<RecyclerView>(R.id.feedList)
                postRecyclerView.adapter = PostRecyclerViewAdapter(
                    postList!!,
                    LayoutInflater.from(activity),
                    Glide.with(activity!!),
                    this@PostFragment,
                    activity as (MainActivity)
                )
            }

            override fun onFailure(call: Call<ArrayList<InstaPost>>, t: Throwable) {
            }
        })
    }

    class PostRecyclerViewAdapter(
        val postList : ArrayList<InstaPost>,
        val inflater: LayoutInflater,
        val glide: RequestManager,
        val postFragment: PostFragment,
        val activity: MainActivity
    ): RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val petImg : ImageView
            val petName : TextView
            val postUserName : TextView
            val postImg : ImageView
            val postContent : TextView
            val favoriteBtn : ImageButton
            val favoriteColorBtn : ImageButton

            init{
                petImg = itemView.findViewById(R.id.petImg)
                petName = itemView.findViewById(R.id.userName)
                postUserName = itemView.findViewById(R.id.postUserName)
                postImg = itemView.findViewById(R.id.postImg)
                postContent = itemView.findViewById(R.id.postContent)
                favoriteBtn = itemView.findViewById(R.id.favoriteBtn) //좋아요 버튼
                favoriteColorBtn = itemView.findViewById(R.id.favoriteColorBtn)

                favoriteBtn.setOnClickListener {
                    postFragment.postLike(postList.get(adapterPosition).id)
                    activity.runOnUiThread {
                        favoriteColorBtn.visibility = VISIBLE
                    }
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.post_recyclerview_item,parent,false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val post = postList.get(position)

            post.owner_profile.image?.let{
                glide.load(it).centerCrop().circleCrop().into(holder.petImg)
            }
            post.image.let{
                glide.load(it).centerCrop().into(holder.postImg)
            }
            holder.postUserName.text = post.owner_profile.username
            holder.postContent.text = post.content
        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }
}
