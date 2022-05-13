package com.bagooni.petmliy_android_app.post.Comment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentCommentBinding
import com.bagooni.petmliy_android_app.post.Retrofit.Comment
import com.bagooni.petmliy_android_app.post.Retrofit.CommentRetrofitService
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.System.load

class CommentFragment : Fragment(){
    private var _binding: FragmentCommentBinding?=null
    private val binding get() = _binding!!
    private var personEmailInput : String = ""
    private var userImgUri : String = ""
    lateinit var retrofitService: CommentRetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val acct = GoogleSignIn.getLastSignedInAccount(activity as MainActivity)
        if (acct != null) {
            val personEmail = acct.email
            val usrImg = acct.photoUrl
            personEmailInput = personEmail.toString()
            userImgUri = usrImg.toString()
            Log.d("google",personEmailInput)
        }

        val CommentListView = view.findViewById<RecyclerView>(R.id.commentList)
        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitService = retrofit.create(CommentRetrofitService::class.java)

//        userImgUri?.let {
//            Glide.load(it).centerCrop().circleCrop().into(binding.userImg)
//        }
    }

    class CommentRecyclerViewAdapter(
        val commentList : ArrayList<Comment>,
        val inflater: LayoutInflater,
        val activity: MainActivity,
        val glide: RequestManager
    ): RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder>(){

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val petName : TextView
            val petImg : ImageView
            val commentText : TextView

            init {
                petName = itemView.findViewById(R.id.petName)
                petImg = itemView.findViewById(R.id.userImg)
                commentText = itemView.findViewById(R.id.commentText)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.post_comment_item_detail,parent,false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val comment = commentList.get(position)

            comment.userImg?.let {
                glide.load(it).centerCrop().circleCrop().into(holder.petImg)
            }
            holder.petName.text = comment.username
            holder.commentText.text = comment.comment
        }

        override fun getItemCount(): Int {
            return commentList.size
        }
    }
}