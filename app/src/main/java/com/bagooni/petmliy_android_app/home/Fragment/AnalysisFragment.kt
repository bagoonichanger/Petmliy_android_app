package com.bagooni.petmliy_android_app.home.Fragment

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalysisBinding.inflate(inflater,container,false)
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            }
        binding.selectPicture.setOnClickListener{
            imagePickerLauncher.launch(
                Intent(Intent.ACTION_PICK).apply {
                    this.type = MediaStore.Images.Media.CONTENT_TYPE
                }
            )
        }
        return binding.root
    }

}