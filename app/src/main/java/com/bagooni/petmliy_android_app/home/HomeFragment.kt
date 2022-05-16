package com.bagooni.petmliy_android_app.home

import android.annotation.SuppressLint
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentHomeBinding
import com.bagooni.petmliy_android_app.home.Weather.WeatherModel
import com.bagooni.petmliy_android_app.home.Weather.WeatherViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONObject

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var viewModel: WeatherViewModel = WeatherViewModel()

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
//            val idToken = account.idToken
            updateUI(account)
        } catch (e: ApiException) {
            Log.w("Google", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            val personName = account.displayName
            val personEmail = account.email
            val personPhoto = account.photoUrl

            if (personPhoto != null) {
                Glide
                    .with(binding.personImage.context)
                    .load(personPhoto.toString())
                    .circleCrop()
                    .into(binding.personImage)
            }

            binding.signInButton.visibility = View.GONE
            binding.signInButton.isEnabled = false
            binding.statusText.text = "Good Morning, ${personName}"
            binding.statusSubText.text = "${personEmail}"
            binding.noneImage.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
            binding.logoutButton.isEnabled = true
            binding.googleIcon.visibility = View.VISIBLE
            binding.logoutText.visibility = View.VISIBLE
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
                    return@registerForActivityResult
                }
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
    }

    private fun googleSet() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener {
            val intent = mGoogleSignInClient?.signInIntent
            activityResultLauncher.launch(intent)
        }
        binding.analysisButton.setOnClickListener {
            findNavController().navigate(R.id.albumFragment)
        }
        binding.placeButton.setOnClickListener {
            findNavController().navigate(R.id.bookMarkFragment)
        }
        initWeatherView()
        observeData()
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            updateUI(account)
        }
        binding.logoutButton.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initWeatherView(){
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        var jsonObject = JSONObject()
        jsonObject.put("url", getString(R.string.weather_url))
        jsonObject.put("path","weather")
        jsonObject.put("q","Seoul")
        jsonObject.put("appid",getString(R.string.weather_app_id))
        viewModel.getWeatherInfoView(jsonObject)
    }

    private fun observeData(){
        viewModel.isSuccWeather.observe(
            viewLifecycleOwner, Observer { it ->
                if(it) {
                    viewModel.responseWeather.observe(
                        viewLifecycleOwner, Observer { setWeatherData(it) }
                    )
                }
            }
        )
    }

    private fun setWeatherData(model: WeatherModel){
        val temp = model.main.temp!!.toDouble() - 273.15
        val weatherImgUrl = "http://openweathermap.org/img/w/"+model.weather[0].icon+".png"
        binding.currentTemp.text = StringBuilder().append(String.format("%.2f", temp)).append(" 'C").toString()
        binding.currentMain.text = model.weather[0].main
        binding.windSpeed.text = StringBuilder().append(model.wind.speed).append(" m/s").toString()
        binding.cloudCover.text = StringBuilder().append(model.clouds.all).append(" %").toString()
        binding.humidity.text = StringBuilder().append(model.main.humidity).append(" %").toString()
        Glide.with(this).load(weatherImgUrl).into(binding.weatherImg)
    }

}