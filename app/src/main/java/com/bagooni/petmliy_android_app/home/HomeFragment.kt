package com.bagooni.petmliy_android_app.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            Log.d("Google", "3")
            val account = completedTask.getResult(ApiException::class.java)
//            val idToken = account.idToken

            // TODO(developer): send ID Token to server and validate

            updateUI(account)
        } catch (e: ApiException) {
            Log.w("Google", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            val personName = account.displayName
            val personEmail = account.email
            val personPhoto = account.photoUrl
            // TODO: token값 전달ㄹ???
            // TODO: 이름 값 전달

            binding.petName.text = personName
            binding.petBirth.text = personEmail
            if (personPhoto != null) {
                Glide
                    .with(binding.petImage.context)
                    .load(personPhoto.toString())
                    .circleCrop()

                    .into(binding.petImage)
            }

            binding.signInButton.visibility = View.GONE
            binding.petName.visibility = View.VISIBLE
            binding.petBirth.visibility = View.VISIBLE
            binding.testText.text = "오늘 하루도 수고 했어요"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLauncher()
        googleSet()
    }

    private fun initLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != AppCompatActivity.RESULT_OK) {
                    Log.d("Google", "1")
                    return@registerForActivityResult
                }
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
    }

    private fun googleSet() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("888676227247-keki43t7at854brv89r5oh1lnsvu7ec1.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener {
            val intent = mGoogleSignInClient?.signInIntent
            Log.d("Google", intent.toString())
            activityResultLauncher.launch(intent)
        }
        binding.mypageButton.setOnClickListener {
//            findNavController().navigate()
        }
        binding.albumButton.setOnClickListener {
//            findNavController().navigate()
        }
        binding.bookmarkButton.setOnClickListener {
//            findNavController().navigate()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null && account.id != null) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}