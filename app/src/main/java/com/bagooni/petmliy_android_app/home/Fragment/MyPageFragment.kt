package com.bagooni.petmliy_android_app.home.Fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.bagooni.petmliy_android_app.MainActivity
import com.bagooni.petmliy_android_app.R
import com.bagooni.petmliy_android_app.databinding.FragmentMyPageBinding
import com.bagooni.petmliy_android_app.home.Retrofit.MypageRetrofitService
import com.bagooni.petmliy_android_app.post.UserInfo
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding?=null
    private val binding get() = _binding!!
    private val RC_SIGN_IN = 123
    var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater,container,false)
        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_homeFragment)
        }
        //oo
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity as MainActivity, gso)

        binding.signInButton.setOnClickListener{ signIn(); signOutLayout()}
        binding.logoutButton.setOnClickListener{ signOut() }

        return binding.root
    }

    private fun saveMyPage(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(MypageRetrofitService::class.java)

        //헤더
        val header = HashMap<String,String>()
        val sp = (activity as MainActivity).getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val token = sp.getString("token","")

        val glide = Glide.with(this)

        header.put("Authorization","token" + token!!) //헤더 Authorization에 token 값 전송
        retrofitService.getUserInfo(header).enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if(response.isSuccessful){
                    val userInfo : UserInfo = response.body()!!
                    userInfo.profile.image?.let {
                        glide.load(it).into(binding.petImage)
                    }
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut(){
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(activity as MainActivity) {
                signInLayout()
            }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(activity as MainActivity)
        if (account == null){
            signInLayout()
        }else{
            signOutLayout()
        }
    }

    private fun signInLayout(){
        binding.signInButton.visibility = View.VISIBLE
        binding.signInLayout.visibility = View.VISIBLE
        binding.saveButton.visibility = View.INVISIBLE
        binding.logoutButton.visibility = View.INVISIBLE
    }
    private fun signOutLayout(){
        binding.signInButton.visibility = View.INVISIBLE
        binding.signInLayout.visibility = View.INVISIBLE
        binding.saveButton.visibility = View.VISIBLE
        binding.logoutButton.visibility = View.VISIBLE
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acct = completedTask.getResult(ApiException::class.java)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto = acct.photoUrl
                Log.d(ContentValues.TAG, "handleSignInResult:personName $personName")
                Log.d(ContentValues.TAG, "handleSignInResult:personGivenName $personGivenName")
                Log.d(ContentValues.TAG, "handleSignInResult:personEmail $personEmail")
                Log.d(ContentValues.TAG, "handleSignInResult:personId $personId")
                Log.d(ContentValues.TAG, "handleSignInResult:personFamilyName $personFamilyName")
                Log.d(ContentValues.TAG, "handleSignInResult:personPhoto $personPhoto")
            }
        } catch (e: ApiException) {
            Log.e(ContentValues.TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((activity as MainActivity).currentFocus?.windowToken, 0)
        return true
    }

}