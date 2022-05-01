package com.bagooni.petmliy_android_app.walk.Fragment.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.databinding.WalkRecycleviewDetailBinding
import com.bagooni.petmliy_android_app.map.model.documents.PlaceModel
import com.bagooni.petmliy_android_app.walk.Db.Tracking
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.bumptech.glide.Glide

class CalendarRecyclerAdapter(val detailRecyclerView: (Tracking) -> Unit) :
    ListAdapter<Tracking, CalendarRecyclerAdapter.ItemViewHolder>(CalendarRecyclerAdapter.differ) {
    inner class ItemViewHolder(private val binding: WalkRecycleviewDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tracking: Tracking){
            val trackingImageView = binding.trackingImage
            val trackingDateTextView = binding.trackingDate
            val trackingAvgSpeedTextView = binding.trackingAvgSpeed
            val trackingDistanceTextView = binding.trackingDistance
            val trackingTimeTextView = binding.trackingTime
            val trackingCaloriesTextView = binding.trackingCalories

            Glide
                .with(trackingImageView.context)
                .load(tracking.img)
                .into(trackingImageView)

            trackingDateTextView.text = "${tracking.year}/${tracking.month}/${tracking.day}"

            val avgSpeed = "${tracking.avgSpeedInKMH}km/h"
            trackingAvgSpeedTextView.text = avgSpeed

            val distanceInKm = "${tracking.distanceInMeters / 1000f}km"
            trackingDistanceTextView.text = distanceInKm

            trackingTimeTextView.text = TrackingUtility.getFormattedStopWatchTime(tracking.timeInMillis)

            val caloriesBurned = "${tracking.caloriesBurned}kcal"
            trackingCaloriesTextView.text = caloriesBurned

            binding.root.setOnClickListener {
                detailRecyclerView(tracking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(WalkRecycleviewDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<Tracking>() {
            override fun areItemsTheSame(oldItem: Tracking, newItem: Tracking): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tracking, newItem: Tracking): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}
