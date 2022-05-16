package com.bagooni.petmliy_android_app.home.Fragment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.databinding.LikeplaceRecycleviewDetailBinding
import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto


class LikePlaceRecyclerAdapter(
    val deleteButton: (LikePlaceDto) -> Unit,
) : ListAdapter<LikePlaceDto, LikePlaceRecyclerAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(private val binding: LikeplaceRecycleviewDetailBinding) :
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

            binding.recyclerViewDeleteButton.setOnClickListener {
                deleteButton(placeModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LikeplaceRecycleviewDetailBinding.inflate(
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
                return oldItem.placeId == newItem.placeId
            }

            override fun areContentsTheSame(oldItem: LikePlaceDto, newItem: LikePlaceDto): Boolean {
                return oldItem == newItem
            }

        }
    }
}
