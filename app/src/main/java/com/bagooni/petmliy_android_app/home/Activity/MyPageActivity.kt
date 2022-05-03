package com.bagooni.petmliy_android_app.home.Activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.bagooni.petmliy_android_app.databinding.ActivityMyPageBinding
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


class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private val RC_SIGN_IN = 123
    var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.closeButton.setOnClickListener{ finish() }
        binding.signInButton.setOnClickListener{ signIn(); signOutLayout()}
        binding.logoutButton.setOnClickListener{ signOut() }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(MypageRetrofitService::class.java)

        //헤더
        val header = HashMap<String,String>()
        val sp = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val token = sp.getString("token","")

        val glide = Glide.with(this)

        header.put("Authorization","token" + token!!) //헤더 Authorization에 token 값 전송
        retrofitService.getUserInfo(header).enqueue(object : Callback<UserInfo>{
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
            .addOnCompleteListener(this) {
                signInLayout()
            }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null){
            signInLayout()
        }else{
            signOutLayout()
        }
    }

    private fun signInLayout(){
        binding.signInButton.visibility = VISIBLE
        binding.signInLayout.visibility = VISIBLE
        binding.saveButton.visibility = INVISIBLE
        binding.logoutButton.visibility = INVISIBLE
    }
    private fun signOutLayout(){
        binding.signInButton.visibility = INVISIBLE
        binding.signInLayout.visibility = INVISIBLE
        binding.saveButton.visibility = VISIBLE
        binding.logoutButton.visibility = VISIBLE
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
                Log.d(TAG, "handleSignInResult:personName $personName")
                Log.d(TAG, "handleSignInResult:personGivenName $personGivenName")
                Log.d(TAG, "handleSignInResult:personEmail $personEmail")
                Log.d(TAG, "handleSignInResult:personId $personId")
                Log.d(TAG, "handleSignInResult:personFamilyName $personFamilyName")
                Log.d(TAG, "handleSignInResult:personPhoto $personPhoto")
            }
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }
}