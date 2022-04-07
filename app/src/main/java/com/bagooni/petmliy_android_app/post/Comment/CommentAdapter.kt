package com.bagooni.petmliy_android_app.post.Comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.R

class CommentAdapter(val comments : ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.apply {  }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.post_comment_item_detail,
            parent,false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(v:View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        fun bind(listener: View.OnClickListener, item: Comment){
        }
    }
}