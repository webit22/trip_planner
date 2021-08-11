package com.example.tripplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tripplanner.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"
    private var callback : SessionCallback = SessionCallback(this)

    private val RC_SIGN_IN = 1000 // Google Login Result
    private var googleSignInClient : GoogleSignInClient? = null // Google Api Client

    private lateinit var fbAuth : FirebaseAuth // Firebase Auth
    private var customToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [START initialize_auth]
        // Initialize Firebase Auth
        fbAuth = Firebase.auth
        // [END initialize_auth]

        binding.btnGoogleLogin.setOnClickListener {
            googleLoginStart()
        }
        binding.btnKakaoLogin.setOnClickListener {
            kakaoLoginStart()
        }
        binding.btnStart.setOnClickListener {
            startMainActivity()
        }
    }

    // [on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = fbAuth.currentUser
        updateUI(currentUser)
    }

    // Update UI based on Firebase's current user. Show Login Button if not logged in.
    private fun updateUI(user: FirebaseUser?) {
        if(user != null) { // User is signed in
            // UI ver.1 - 시작하기 버튼 하나만 구성. loginbtn 2개 invisible
            binding.btnStart.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.GONE
            binding.btnKakaoLogin.visibility = View.GONE
        } else{
            // UI ver.2 - loginbtn 2개로 구성. 시작하기 버튼 invisible
            binding.btnStart.visibility = View.GONE
            binding.btnGoogleLogin.visibility = View.VISIBLE
            binding.btnKakaoLogin.visibility = View.VISIBLE
        }
    }


    /* KAKAO LOGIN */
    private fun kakaoLoginStart(){
        // keyHash 발급
        val keyHash = Utility.getKeyHash(this)
        Log.d(TAG, "KEY_HASH : $keyHash")

        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this)

        // 로그인 공통 callback (login 결과에 대한 코드)
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            Log.d(TAG, "LoginActivity - kakaoLoginStart() called")
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "Toss accessToken to Firebase / AccessToken : ${token.accessToken}")
                val kakaoToken = token.accessToken
                fbAuthKakao(kakaoToken) // accesstoken, user 정보 같이 넘겨야하나?? 아니면 토큰만?

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

    private fun fbAuthKakao(accessToken: String) {

        // 1. firebase에 accessToken 전송
        // 2. fbAuth에 user를 생성
        // 3. fb custom token 발급받아서 최종 로그인 처리 (카카오 계정이 firebase에 등록됨)

        // Initiate sign in with custom token
        customToken?.let {
            fbAuth.signInWithCustomToken(it)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCustomToken:success")
                        val user = fbAuth.currentUser

                        // use FirebaseUser.getIdToken() value to authenticate with your backend server

                        if (user != null) {
                            Log.d(TAG, "로그인 성공")
                        }
                        startMainActivity()

                    } else {
                        Log.w(TAG, "로그인 실패", task.exception)
                        Toast.makeText(App.instance, "로그인 실패", Toast.LENGTH_SHORT).show()

                        updateUI(null)
                    }
                }
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

        Log.d(TAG, "LoginActivity - onActivityResult() called")

        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            Log.i(TAG, "Session get current session")
            return
        }
        //https://jslee-tech.tistory.com/4

        super.onActivityResult(requestCode, resultCode, data)
        // Google Login Activity
        if(requestCode === RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                // 구글 로그인 성공; Firebase Auth 진행
                val account = task.getResult(ApiException::class.java)
                fbAuthGoogle(account)
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 정상적으로 로그인하면 GoogleSignInAccount 객체로부터 ID token을 가져와서 FB 사용자 인증 정보로 교환.
    // FB 사용자 인증 정보를 사용해 FB에 인증.
    private fun fbAuthGoogle(acct : GoogleSignInAccount){
        Log.d(TAG, "LoginActivity - firebaseAuthWithGoogle() called")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        fbAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
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

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }
}