package com.bagooni.petmliy_android_app.home.Fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.databinding.BookmarkRecycleviewDetailBinding
import com.bagooni.petmliy_android_app.databinding.MapRecycleviewDetailBinding
import com.bagooni.petmliy_android_app.databinding.WalkRecycleviewDetailBinding
import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto
import com.bagooni.petmliy_android_app.map.model.documents.PlaceModel
import com.bagooni.petmliy_android_app.walk.Db.Tracking
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.bumptech.glide.Glide


class BookMarkRecyclerAdapter(
    val shareButton: (LikePlaceDto) -> Unit,
) : ListAdapter<LikePlaceDto, BookMarkRecyclerAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(private val binding: BookmarkRecycleviewDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(placeModel: LikePlaceDto) {
            val titleTextView = binding.titleTextView
            val addressTextView = binding.addressTextView
            val callNumberTextView = binding.callNumberTextView
            val categoryTextView = binding.categoryTextView

            titleTextView.text = placeModel.placeName
            addressTextView.text = placeModel.address
            if(placeModel.phone == ""){
                callNumberTextView.text = "전화번호가 없습니다."
            } else {
                callNumberTextView.text = placeModel.phone
            }
            categoryTextView.text = placeModel.categories

            binding.recyclerViewShareButton.setOnClickListener {
                shareButton(placeModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BookmarkRecycleviewDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<LikePlaceDto>() {
            override fun areItemsTheSame(oldItem: LikePlaceDto, newItem: LikePlaceDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LikePlaceDto, newItem: LikePlaceDto): Boolean {
                return oldItem == newItem
            }

        }
    }
}
