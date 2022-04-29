package com.bagooni.petmliy_android_app.walk.Fragment

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentDetailTrackingBinding
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.template.model.Content
import java.io.ByteArrayOutputStream

class DetailTrackingFragment : Fragment(R.layout.fragment_detail_tracking) {
    private val args by navArgs<DetailTrackingFragmentArgs>()
    private var _binding: FragmentDetailTrackingBinding? = null
    private val binding get() = _binding!!


    private val viewModel by lazy {
        ViewModelProvider(this, TrackingViewModel.Factory(requireActivity().application)).get(
            TrackingViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailTrackingBinding.bind(view)

        val date = "${args.tracking.year}년 ${args.tracking.month}월 ${args.tracking.day}일"
        Glide
            .with(binding.detailImageView.context)
            .load(args.tracking.img)
            .into(binding.detailImageView)

        view.findViewById<TextView>(R.id.detailDate).text = date
        view.findViewById<TextView>(R.id.detailDistance).text =
            args.tracking.distanceInMeters.toString()
        view.findViewById<TextView>(R.id.detailVelocity).text =
            args.tracking.avgSpeedInKMH.toString()
        view.findViewById<TextView>(R.id.detailCalorie).text =
            args.tracking.caloriesBurned.toString()
        view.findViewById<TextView>(R.id.detailWalkTime).text =
            TrackingUtility.getFormattedStopWatchTime(args.tracking.timeInMillis)

        initButtons()
    }

    private fun initButtons() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailTrackingFragment_to_walkFragment)
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteTracking(args.tracking)
            Snackbar.make(it, "산책기록이 삭제되었습니다.", Snackbar.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_detailTrackingFragment_to_walkFragment)
        }
        binding.shareButton.setOnClickListener {
            bitmapToUri(args.tracking.img)
        }
    }

    private fun bitmapToUri(bitmap: Bitmap?) {
        val fileName = "${System.currentTimeMillis()}.png"
        val resolver = requireContext().contentResolver
        val imageCollections =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollections, imageDetails)
        imageUri ?: return

        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }


        val sharing_intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
        }
        startActivity(Intent.createChooser(sharing_intent, "공유하기"))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}