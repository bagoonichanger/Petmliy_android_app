package com.bagooni.petmliy_android_app.walk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.walk.Db.Tracking
import com.bagooni.petmliy_android_app.walk.Db.TrackingViewModel
import com.bagooni.petmliy_android_app.walk.Fragment.Adapter.CalendarRecyclerAdapter
import com.bagooni.petmliy_android_app.walk.Fragment.Api.CustomWalkApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class WalkFragment : Fragment(R.layout.fragment_walk) {
    var client: OkHttpClient? =
        httpLoggingInterceptor()?.let { OkHttpClient.Builder().addInterceptor(it).build() }

    private var googleEmail: String? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var setDate = mutableListOf<CalendarDay>()

    private val recyclerAdapter = CalendarRecyclerAdapter(detailRecyclerView = {
        findNavController().navigate(
            WalkFragmentDirections.actionWalkFragmentToDetailTrackingFragment(
                tracking = it
            )
        )
    })

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
                    Log.d("Google", "1")
                    return@registerForActivityResult
                }
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
    }

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

    private fun googleSet() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("888676227247-keki43t7at854brv89r5oh1lnsvu7ec1.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
        val formatted = current.format(formatter)
        view.findViewById<TextView>(R.id.calendarTitle).text = formatted

        val recyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity).also{
            it.reverseLayout = true
            it.stackFromEnd = true
        }

        initButtons(view)

        var calendarView = view.findViewById<MaterialCalendarView>(R.id.calendarView)

        calendarView.setSelectedDate(CalendarDay.today())
        calendarView.setOnDateChangedListener { widget, date, selected ->
            view.findViewById<TextView>(R.id.calendarTitle).text =
                "${date.year}/${date.month}/${date.day}"
            viewModel.trackingSortedByCalendar(date.year, date.month, date.day)
                .observe(viewLifecycleOwner, Observer {
                    Log.d("check", "${date.year}/${date.month}/${date.day}")
//                    recyclerAdapter.submitList(it)
                    customAPi(date.year, date.month, date.day)
                })

        }
        val calendar = Calendar.getInstance()
        viewModel.trackingSortedByCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        ).observe(viewLifecycleOwner, Observer {
//            recyclerAdapter.submitList(it)
            Log.d("chicken","yes")
            allDataAPi(calendarView)
            customAPi(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
        })

//        val r = Runnable {
//            val iterator = viewModel.allTracking().iterator() //날짜 상관없이 모든 데이터 가져오기
//            while (iterator.hasNext()) {
//                var item = iterator.next()
//                var year = item.year;
//                var month = item.month;
//                var day = item.day
//
//                setDate.add(CalendarDay.from(year, month, day))
//            }
//            activity?.runOnUiThread {
//                calendarView.addDecorator(object : DayViewDecorator {
//                    override fun shouldDecorate(day: CalendarDay?): Boolean {
//                        return setDate.contains(day)
//                    }
//
//                    override fun decorate(view: DayViewFacade?) {
//                        view?.addSpan(DotSpan(5f, Color.RED))
//                    }
//                })
//            }
//        }
//        val thread = Thread(r)
//        thread.start()
    }

    private fun customAPi(year:Int, month:Int, day:Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CustomWalkApi::class.java)
        val searchWalk = googleEmail?.let { email ->
            api.searchData(email, year, month, day)
        }

        if (searchWalk != null) {
            searchWalk.enqueue(object : Callback<List<Tracking>> {
                override fun onResponse(
                    call: Call<List<Tracking>>,
                    response: Response<List<Tracking>>
                ) {
                    response.body().let{ dto ->
                        Log.d("Walk",  response.body().toString())
                        recyclerAdapter.submitList(dto)
                    }
                }

                override fun onFailure(call: Call<List<Tracking>>, t: Throwable) {
                    Log.d("walk Error", t.message.toString())
                }

            })
        }
    }

    private fun allDataAPi(calendarView: MaterialCalendarView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-54-180-166-236.ap-northeast-2.compute.amazonaws.com:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CustomWalkApi::class.java)
        val searchAllWalk = googleEmail?.let { email ->
            api.searchAllData(email)
        }

        if (searchAllWalk != null) {
            searchAllWalk.enqueue(object : Callback<List<Tracking>> {
                override fun onResponse(
                    call: Call<List<Tracking>>,
                    response: Response<List<Tracking>>
                ) {
                    response.body().let{ dto ->
                        Log.d("Walk",  response.body().toString())
                        val r = Runnable {
                            val iterator = dto?.iterator() //날짜 상관없이 모든 데이터 가져오기
                            if (iterator != null) {
                                while (iterator.hasNext()) {
                                    var item = iterator.next()
                                    var year = item.year;
                                    var month = item.month;
                                    var day = item.day

                                    setDate.add(CalendarDay.from(year, month, day))
                                }
                            }
                            activity?.runOnUiThread {
                                if (calendarView != null) {
                                    calendarView.addDecorator(object : DayViewDecorator {
                                        override fun shouldDecorate(day: CalendarDay?): Boolean {
                                            return setDate.contains(day)
                                        }

                                        override fun decorate(view: DayViewFacade?) {
                                            view?.addSpan(DotSpan(5f, Color.RED))
                                        }
                                    })
                                }
                            }
                        }
                        val thread = Thread(r)
                        thread.start()
                    }
                }

                override fun onFailure(call: Call<List<Tracking>>, t: Throwable) {
                    Log.d("walk Error", t.message.toString())
                }

            })
        }
    }

    private fun initButtons(view: View) {
        view.findViewById<FloatingActionButton>(R.id.changeTrackingFragment).setOnClickListener {
            getPermissions()
            findNavController().navigate(R.id.action_walkFragment_to_trackingFragment)
        }
    }

    private fun getPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            Log.d("oncrate", "check")
            updateUI(account)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_AND_WRITE_EXTERNAL_STORAGE_PERMISSION =
            100
    }
}


