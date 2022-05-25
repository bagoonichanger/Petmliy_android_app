package com.bagooni.petmliy_android_app.walk.Fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentDetailTrackingBinding
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.walk.Fragment.Api.CustomWalkApi
import com.bagooni.petmliy_android_app.walk.Fragment.Service.TrackingUtility
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailTrackingFragment : Fragment(R.layout.fragment_detail_tracking) {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }

    private val args by navArgs<DetailTrackingFragmentArgs>()
    private var _binding: FragmentDetailTrackingBinding? = null
    private val binding get() = _binding!!

    private var googleEmail: String? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val viewModel by lazy {
        ViewModelProvider(this, TrackingViewModel.Factory(requireActivity().application)).get(
            TrackingViewModel::class.java
        )
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
                    return@registerForActivityResult
                }
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w("Google", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d("map", account.email.toString())
            googleEmail = account.email
        }
    }

    private fun googleSet() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailTrackingBinding.bind(view)

        val date = "${args.tracking.year}년 ${args.tracking.month}월 ${args.tracking.day}일"

        val bytes = Base64.decode(args.tracking.img, Base64.DEFAULT)
        val changeImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        Glide
            .with(binding.detailImageView.context)
            .load(changeImg)
            .into(binding.detailImageView)

        view.findViewById<TextView>(R.id.detailDate).text = date
        view.findViewById<TextView>(R.id.detailDistance).text =
            (args.tracking.distanceInMeters/1000f).toString()
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
//            viewModel.deleteTracking(args.tracking)
            Snackbar.make(it, "산책 기록이 삭제되었습니다.", Snackbar.LENGTH_LONG).show()
            customAPi(args.tracking.id, args.tracking.year, args.tracking.month, args.tracking.day)
            findNavController().navigate(R.id.action_detailTrackingFragment_to_walkFragment)
        }
        binding.shareButton.setOnClickListener {
//            val bytes = Base64.decode(args.tracking.img, Base64.DEFAULT)
//            val changeImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            getBitmapFromView(requireView(), requireActivity()) { bitmap ->
                bitmapToUri(bitmap)
            }
//            bitmapToUri(changeImg)
        }
    }

    private fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                        // possible to handle other result codes ...
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }

    private fun customAPi(id: Int, year: Int, month: Int, day: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CustomWalkApi::class.java)

        googleEmail?.let { email ->
            api.deleteDate(email, id, year, month, day)
        }?.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (!response.isSuccessful) {
                    viewModel.deleteTracking(args.tracking)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("walk Error", t.message.toString())
            }

        })
    }

    private fun bitmapToUri(bitmap: Bitmap?) {
        val fileName = "${System.currentTimeMillis()}.jpeg"
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
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollections, imageDetails)
        imageUri ?: return

        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }


        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
        }
        startActivity(Intent.createChooser(sharingIntent, "공유하기"))
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            updateUI(account)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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