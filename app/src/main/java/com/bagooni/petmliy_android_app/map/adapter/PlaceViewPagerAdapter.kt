package com.bagooni.petmliy_android_app.map.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.databinding.MapViewpagerDetailBinding
import com.bagooni.petmliy_android_app.map.model.documents.PlaceModel

class PlaceViewPagerAdapter(
    val shareButton: (PlaceModel) -> Unit,
    val likeButton: (PlaceModel) -> Unit
) : ListAdapter<PlaceModel, PlaceViewPagerAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(private val binding: MapViewpagerDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(placeModel: PlaceModel) {
            val titleTextView = binding.titleTextView
            val addressTextView = binding.addressTextView
            val callNumberTextView = binding.callNumberTextView
            val categoryTextView = binding.categoryTextView

            titleTextView.text = placeModel.place_name
            addressTextView.text = placeModel.address_name
            callNumberTextView.text = placeModel.phone
            categoryTextView.text = placeModel.category_name

            binding.viewpagerShareButton.setOnClickListener {
                shareButton(placeModel)
            }
            binding.viewPagerLikeButton.setOnClickListener {
                likeButton(placeModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            MapViewpagerDetailBinding.inflate(
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
        val differ = object : DiffUtil.ItemCallback<PlaceModel>() {
            override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}