package com.example.tripplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tripplanner.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"

    private val RC_SIGN_IN = 1000 // Google Login Result
    private var fbAuth : FirebaseAuth? = null // Firebase Auth
    private var googleSignInClient : GoogleSignInClient? = null // Google Api Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoogleLogin.setOnClickListener {
            googleLoginStart()
        }
        binding.btnKakaoLogin.setOnClickListener {
            kakaoLoginStart()
        }
    }

    /* KAKAO LOGIN */
    private fun kakaoLoginStart(){
        // val keyHash = Utility.getKeyHash(this)
        // Log.d(TAG, "KEY_HASH : $keyHash")

        // 로그인 공통 callback (login 결과에 대한 코드) 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            Log.d(TAG, "LoginActivity - kakaoLoginStart() called")
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 : ${token.accessToken}")
                startMainActivity()
            }
        }

        // Login. 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }


    /* GOOGLE LOGIN */
    private fun googleLoginStart(){
        Log.d(TAG, "LoginActivity - googleLoginStart() called")

        // BEGIN config_signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // END

        fbAuth = FirebaseAuth.getInstance() // FB 인증을 사용할 수 있게 초기화

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 계정 인증 Activity가 보여짐
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "LoginActivity - onActivityResult() called")
        // 구글 로그인
        if(requestCode === RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                // 구글 로그인 성공; Firebase Auth 진행
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 정상적으로 로그인하면 GoogleSignInAccount 객체로부터 ID token을 가져와서 FB 사용자 인증 정보로 교환.
    // FB 사용자 인증 정보를 사용해 FB에 인증.
    private fun firebaseAuthWithGoogle(acct : GoogleSignInAccount){
        Log.d(TAG, "LoginActivity - firebaseAuthWithGoogle() called")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        fbAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val user = fbAuth?.currentUser

                    Log.d(TAG, "로그인 성공 : ${acct.id}")
                    startMainActivity()
                }else{
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "로그인 실패", it.exception)
                }
            }
    }

    private fun startMainActivity(){
        Log.d(TAG, "LoginActivity - startMainActivity() called")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}