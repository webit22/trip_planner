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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.auth.authorization.accesstoken.AccessToken
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

abstract class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"
    // 로그인 공통 callback (login 결과를 SessionCallback.kt 으로 전송)
    private var callback : SessionCallback = SessionCallback(this)

    private val RC_SIGN_IN = 1000 // Google Login Result
    private var googleSignInClient : GoogleSignInClient? = null // Google Api Client

    private lateinit var fbAuth : FirebaseAuth // Firebase Auth

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

        // Session : login 상태를 유지시켜주는 객체 (accessToken을 관리함)
        Session.getCurrentSession().addCallback(callback) // 세션 상태 변화 콜백을 받고자 할때 콜백을 등록
        // authType - 인증받을 타입; callerActivity - 세션오픈을 호출한 activity
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this) // 세션 오픈을 진행
    }

    // login 버튼 클릭 -> 세션 연결됨 -> 파이어베이스에 토큰 전달 및 사용자 인증 완료
    fun fbAuthKakao(accessToken: AccessToken) {

        // 사용자가 앱에 로그인하면 사용자의 로그인 인증 정보(예: 사용자 이름과 비밀번호)를 인증 서버로 전송하세요.
        // 서버가 사용자 인증 정보를 확인하여 정보가 유효하면 커스텀 토큰을 반환합니다.
        // 인증 서버에서 커스텀 토큰을 받은 후 다음과 같이 이 토큰을 signInWithCustomToken에 전달하여 사용자를 로그인 처리합니다.
        // 1. import user로 카카오 정보 등록
        // 2. customtoken 발급??
        val customToken: String? = null
        val user = fbAuth.currentUser

        customToken?.let {
            fbAuth.signInWithCustomToken(it)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCustomToken : 로그인 성공")

                    } else {
                        Log.w(TAG, "로그인 실패", task.exception)
                        Toast.makeText(App.instance, "로그인 실패", Toast.LENGTH_SHORT).show()

                        updateUI(null)
                    }
                }
        }
        // user profile, name, email 정보 전송
        startMainActivity()
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

    // 정상적으로 로그인하면 GoogleSignInAccount 객체로부터 ID token을 가져와서 FB 사용자 인증 정보로 교환.
    // FB 사용자 인증 정보를 사용해 FB에 인증.
    private fun fbAuthGoogle(acct : GoogleSignInAccount){
        Log.d(TAG, "LoginActivity - fbAuthGoogle() called")

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

    // Login Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "LoginActivity - onActivityResult() called")

        // 카카오
        /*
        boolean handleActivityResult()
        로그인 activity를 이용하여 sdk에서 필요로 하는 activity를 띄운다.
        따라서 해당 activity의 결과를 로그인 activity가 받게 된다.
        해당 결과를 세션이 받아서 다음 처리를 할 수 있도록 로그인 activity의 onActivityResult에서 해당 method를 호출한다.
        returns true if the intent originated from Kakao login, false otherwise.
        */

        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            Log.i(TAG, "Session get current session")
            return
        }
        //https://jslee-tech.tistory.com/4

        super.onActivityResult(requestCode, resultCode, data)
        // 구글 로그인 결과
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

    private fun startMainActivity(profile: String, nickname: String, email: String){
        Log.d(TAG, "LoginActivity - startMainActivity() called")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("profile", profile)
        intent.putExtra("nickname", nickname)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback) // 더이상 세션 상태 변화 콜백을 받고 싶지 않을 때 삭제
    }
}