package com.bagooni.petmliy_android_app.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentMapBinding
import com.bagooni.petmliy_android_app.map.adapter.PlaceRecyclerAdapter
import com.bagooni.petmliy_android_app.map.adapter.PlaceViewPagerAdapter
import com.bagooni.petmliy_android_app.map.model.KakaoApi
import com.bagooni.petmliy_android_app.map.model.Response.PlaceDto
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private val currentLocationButton: LocationButtonView by lazy {
        binding.currentLocationButton // 현재 위치 버튼
    }

    private val bottomSheetTitleTextView : TextView by lazy {
        mainActivity.findViewById(R.id.bottomSheetTitleTextView)
    }

    private val viewPager: ViewPager2 by lazy {
        binding.placeViewPager
    }

    private val viewpagerAdapter = PlaceViewPagerAdapter(itemClicked = {
        val intent = Intent()
            .apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${it.place_name} ${it.address_name} ${it.phone} ${it.place_url}")
                type = "text/plain"
            }
        startActivity(Intent.createChooser(intent, null))
    })

    private val recyclerView: RecyclerView by lazy {
         mainActivity.findViewById(R.id.recyclerView)
    }

    private val recyclerAdapter = PlaceRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        searchPlace("동물병원")

        viewPager.adapter = viewpagerAdapter
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun searchPlace(keyword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(KakaoApi::class.java)
        val responsePlace = api.getSearchPlaces(API_KEY, keyword)

        responsePlace.enqueue(object : Callback<PlaceDto> {
            override fun onResponse(
                call: Call<PlaceDto>,
                response: Response<PlaceDto>
            ) {
                if(!response.isSuccessful) return

                response.body()?.let{ dto ->
                    viewpagerAdapter.submitList(dto.documents)
                    recyclerAdapter.submitList(dto.documents)
                    bottomSheetTitleTextView.text = "${dto.documents?.size}개의 장소"
                }
            }

            override fun onFailure(call: Call<PlaceDto>, t: Throwable) {
                Log.d("MapFragment", "통신 실패 : ${t.message}")
            }

        })
    }

    override fun onStart() {
        super.onStart()
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val API_KEY = "KakaoAK 43d7c7d5953cc05cfe4479fb034163e0"
    }

}
