package com.bagooni.petmliy_android_app.map

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentMapBinding
import com.bagooni.petmliy_android_app.map.adapter.PlaceRecyclerAdapter
import com.bagooni.petmliy_android_app.map.adapter.PlaceViewPagerAdapter
import com.bagooni.petmliy_android_app.map.model.Api.CustomMapApi
import com.bagooni.petmliy_android_app.map.model.Api.KakaoApi
import com.bagooni.petmliy_android_app.map.model.Dto.LikePlaceDto
import com.bagooni.petmliy_android_app.map.model.Dto.PlaceDto
import com.bagooni.petmliy_android_app.map.model.documents.PlaceModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback, Overlay.OnClickListener {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    private var googleEmail: String? = null

    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var markers: MutableList<Marker> = ArrayList() // 마커 리스트

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d("map", account.email.toString())
            googleEmail = account.email
        }
    }

    private val currentLocationButton: LocationButtonView by lazy { // 현재 위치 버튼
        binding.currentLocationButton
    }

    private val bottomSheetTitleTextView: TextView by lazy {
        mainActivity.findViewById(R.id.bottomSheetTitleTextView)
    }

    private val viewPager: ViewPager2 by lazy {
        binding.placeViewPager
    }

    private val viewPagerAdapter = PlaceViewPagerAdapter(shareButton = {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(it.place_url)
        }
        startActivity(intent)

    }, likeButton = {
        val name = it.place_name
        val phone = it.phone
        val address = it.address_name
        val url = it.place_url
        val categories = it.category_name

        val data = LikePlaceDto(null, name, phone, address, url, categories)
        customAPi(data)
    })

    private val recyclerView: RecyclerView by lazy {
        mainActivity.findViewById(R.id.recyclerView)
    }

    private val recyclerAdapter = PlaceRecyclerAdapter(shareButton = {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, it.place_url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }, likeButton = {
        val name = it.place_name
        val phone = it.phone
        val address = it.address_name
        val url = it.place_url
        val categories = it.category_name
        val data = LikePlaceDto(null, name, phone, address, url, categories)
        customAPi(data)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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
//            .requestIdToken("888676227247-keki43t7at854brv89r5oh1lnsvu7ec1.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initButtons() // 장소 검색
        clickViewPager() // cardView 클릭시

        viewPager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initButtons() {
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchBar.text.toString()
            if (searchText == "") {
                Toast.makeText(activity, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                deleteMarkers(markers)
                searchPlace(searchText)
            }
        }

        binding.likeButton.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_bookMarkFragment)
        }
    }

    private fun clickViewPager() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        selectedModel.y.toDouble(),
                        selectedModel.x.toDouble()
                    )
                )
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        })
    }

    private fun customAPi(data: LikePlaceDto) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CustomMapApi::class.java)
        val responseLikeList = googleEmail?.let { email ->
            api.sendLikePlaces(email, data)
        }

        if (responseLikeList != null) {
            responseLikeList.enqueue(object : Callback<LikePlaceDto> {
                override fun onResponse(
                    call: Call<LikePlaceDto>,
                    response: Response<LikePlaceDto>
                ) {
                    if (!response.isSuccessful)
                        response.body()?.let { it.address?.let { it1 -> Log.d("chicken", it1) } }
                }
                override fun onFailure(call: Call<LikePlaceDto>, t: Throwable) {
                    Log.d("chicken", t.message.toString())
                }
            })
        }
    }

    private fun searchPlace(keyword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(KakaoApi::class.java)
        val responsePlace = api.getSearchPlaces(API_KEY, keyword, 1, 15)

        responsePlace.enqueue(object : Callback<PlaceDto> {
            override fun onResponse(call: Call<PlaceDto>, response: Response<PlaceDto>) {
                if (!response.isSuccessful) return
                response.body()?.let { dto ->
                    updateMarker(dto.documents)
                    viewPagerAdapter.submitList(dto.documents)
                    recyclerAdapter.submitList(dto.documents)
                    bottomSheetTitleTextView.text = "${dto.documents?.size}개의 장소"
                }
            }
            override fun onFailure(call: Call<PlaceDto>, t: Throwable) {
                Log.d("MapFragment", "통신 실패 : ${t.message}")
            }
        })
    }

    private fun updateMarker(documents: List<PlaceModel>?) {
        documents?.forEach { document ->
            val marker = Marker()
            val lat = document.y.toDouble()
            val lng = document.x.toDouble()

            marker.position = LatLng(lat, lng)
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.rgb(245, 189, 213)
            marker.onClickListener = this // marker 클릭시
            marker.tag = document.id // marker 구분을 위한 태그
            markers.add(marker)
            marker.map = naverMap
        }
    }

    private fun deleteMarkers(markers: MutableList<Marker>) {
        markers.let { Markers ->
            Markers.forEach { marker ->
                marker.map = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            Log.d("oncrate", "check")
            updateUI(account)
        }
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false
        currentLocationButton.map = naverMap
    }

    override fun onClick(overlay: Overlay): Boolean { //마커 클릭시 이벤트
        val selectedModel = viewPagerAdapter.currentList.firstOrNull { document ->
            document.id == overlay.tag
        }

        selectedModel?.let {
            viewPager.currentItem = viewPagerAdapter.currentList.indexOf(it)
        }
        return false
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val API_KEY = "KakaoAK 43d7c7d5953cc05cfe4479fb034163e0"
    }
}
