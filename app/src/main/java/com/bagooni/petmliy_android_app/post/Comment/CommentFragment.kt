package com.bagooni.petmliy_android_app.post.Comment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentCommentBinding
import com.bagooni.petmliy_android_app.post.Retrofit.Comment
import com.bagooni.petmliy_android_app.post.Retrofit.CommentRetrofitService
import com.bagooni.petmliy_android_app.post.Retrofit.postComment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CommentFragment : Fragment(){
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }
    private var _binding: FragmentCommentBinding?=null
    private val binding get() = _binding!!
    private var personEmailInput : String = ""
    private var userImgUri : String = ""
    private var commentContent : String = ""
    private lateinit var commentRetrofitService: CommentRetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentBinding.inflate(inflater,container,false)
        binding.commentlayout.setOnClickListener { hideKeyboard() }
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_commentFragment_to_postFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentArgs by navArgs<CommentFragmentArgs>()
        val inputPostId: Long = commentArgs.postId
        Log.d("postId",inputPostId.toString())
        checkSign()

        val CommentListView = view.findViewById<RecyclerView>(R.id.commentList)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        commentRetrofitService = retrofit.create(CommentRetrofitService::class.java)
        Log.d("onViewCreated postId","${inputPostId.javaClass}")
        commentGet(inputPostId)
        commentUpload(inputPostId)
//        userImgUri?.let {
//            Glide.load(it).centerCrop().circleCrop().into(binding.userImg)
//        }
    }
    private fun checkSign(){
        val acct = GoogleSignIn.getLastSignedInAccount(activity as MainActivity)
        if (acct != null) {
            val personEmail = acct.email
            val usrImg = acct.photoUrl
            personEmailInput = personEmail.toString()
            userImgUri = usrImg.toString()
            Log.d("google",personEmailInput)
        }
    }
    private fun commentGet(postId: Long){
        commentRetrofitService.getComment(postId).enqueue(object : Callback<ArrayList<Comment>>{
            override fun onResponse(
                call: Call<ArrayList<Comment>>,
                response: Response<ArrayList<Comment>>
            ) {
                val commentList = response.body()
                val commentRecyclerView = view?.findViewById<RecyclerView>(R.id.commentList)
                commentRecyclerView?.adapter = commentList?.let{
                    CommentRecyclerViewAdapter(
                        it,
                        LayoutInflater.from(activity),
                        activity as (MainActivity),
                        Glide.with(activity!!)
                    )
                }
            }
            override fun onFailure(call: Call<ArrayList<Comment>>, t: Throwable) {
                Log.d("log",t.message.toString())
            }
        })
    }

    private fun commentUpload(postId : Long){
        binding.uploadButton.setOnClickListener {
            Snackbar.make(requireView(), "포스트 업로드", Snackbar.LENGTH_LONG).show()
//            val ft = (activity as MainActivity).supportFragmentManager.beginTransaction()
//            ft.detach(this).attach(this).commit()
            commentContent = binding.commentEdit.text.toString()
            Log.d("commentContent",commentContent)
            Log.d("postId","${postId.javaClass}")
            val body = postComment(userImgUri,postId,commentContent)
            commentRetrofitService.postComment(personEmailInput,body).enqueue(object : Callback<postComment>{
                override fun onResponse(
                    call: Call<postComment>,
                    response: Response<postComment>
                ) {
                    Log.d("log", response.body().toString())
                }
                override fun onFailure(call: Call<postComment>, t: Throwable) {
                    Log.d("log",t.message.toString())
                }
            })
        }
    }

    class CommentRecyclerViewAdapter(
        val commentList: ArrayList<Comment>,
        val inflater: LayoutInflater,
        val activity: MainActivity,
        val glide: RequestManager
    ): RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val userEmail : TextView
            val userImg : ImageView
            val commentText : TextView

            init {
                userEmail = itemView.findViewById(R.id.userEmail)
                userImg = itemView.findViewById(R.id.userImg)
                commentText = itemView.findViewById(R.id.commentText)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.post_comment_item_detail,parent,false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val comment = commentList.get(position)
            comment.userImg?.let {
                glide.load(it).centerCrop().circleCrop().into(holder.userImg)
            }
            holder.userEmail.text = comment.email.split("@")[0]
            holder.commentText.text = comment.commentContent
        }

        override fun getItemCount(): Int {
            return commentList.size
        }
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