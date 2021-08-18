package com.example.tripplanner

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tripplanner.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import com.kakao.sdk.common.util.Utility
import org.json.JSONObject

// loginActivity - class 앞에 abstract 제거 (make activity class as public)
open class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"
    // 로그인 공통 callback (login 결과를 SessionCallback.kt 으로 전송)
    private lateinit var callback : SessionCallback

    private val RC_SIGN_IN = 1000 // Google Login Result
    private var googleSignInClient : GoogleSignInClient? = null // Google Api Client

    private lateinit var fbAuth : FirebaseAuth // Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "LoginActivity - onCreate() called")

        fbAuth = Firebase.auth // Initialize Firebase Auth
        callback = SessionCallback(this) // Initialize Session

        binding.btnGoogleLogin.setOnClickListener {
            googleLoginStart()
        }
        binding.btnKakaoLogin.setOnClickListener {
            kakaoLoginStart()
        }
        binding.btnStart.setOnClickListener {
            val fbUser = fbAuth.currentUser
            val name = fbUser?.displayName.toString()
            val email = fbUser?.email.toString()

            startMainActivity(name, email)
        }
    }

    public override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginActivity - onStart() called")
        updateUI()
    }

    // Update UI based on Firebase's current user. Show Login Button if not logged in.
    fun updateUI() {
        Log.d(TAG, "LoginActivity - updateUI() called")
        val user = fbAuth.currentUser
        if(user != null) { // User is signed in
            // UI ver.1 - 시작하기 버튼 하나만 구성. loginbtn 2개 invisible
            binding.btnStart.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.GONE
            binding.btnKakaoLogin.visibility = View.VISIBLE
        } else{
            // UI ver.2 - loginbtn 2개로 구성. 시작하기 버튼 invisible
            binding.btnStart.visibility = View.GONE
            binding.btnGoogleLogin.visibility = View.VISIBLE
            binding.btnKakaoLogin.visibility = View.VISIBLE
        }
    }


    /* KAKAO LOGIN */
    private fun kakaoLoginStart(){
        Log.d(TAG, "LoginActivity - kakaoLoginStart() called")
        // keyHash 발급
        val keyHash = Utility.getKeyHash(this)
        Log.d(TAG, "KEY_HASH : $keyHash")

        // Session : login 상태를 유지시켜주는 객체 (accessToken을 관리함)
        Session.getCurrentSession().addCallback(callback) // 세션 상태 변화 콜백을 받고자 할때 콜백을 등록
        // authType - 인증받을 타입; callerActivity - 세션오픈을 호출한 activity
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, this) // 세션 오픈을 진행
    }

    open fun getFirebaseJwt(kakaoAccessToken: String): Task<String> {
        Log.d(TAG, "LoginActivity - getFirebaseJwt() called")
        val source = TaskCompletionSource<String>()
        val queue = Volley.newRequestQueue(this)
        val url = "https://kapi.kakao.com/v2/user/me?secure_resource=true/verifyToken" // resources.getString(R.string.validation_server_domain) +
        val validationObject: HashMap<String?, String?> = HashMap()
        validationObject["token"] = kakaoAccessToken

        // listener가 response를 못받아오나?
        val request: JsonObjectRequest = object : JsonObjectRequest(Method.POST, url,
            JSONObject(validationObject as Map<*, *>),
            Response.Listener { response ->
                try {
                    val firebaseToken = response.getString("firebase_token")
                    source.setResult(firebaseToken)
                } catch (e: Exception) {
                    source.setException(e)
                }
            },
            Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
                source.setException(error)
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap() // Access token retrieved after successful Kakao Login
                //params["token"] = kakaoAccessToken
                params["Authorization"] = String.format("Basic %s", Base64.encodeToString(
                    String.format("%s:%s", "token", kakaoAccessToken).toByteArray(), Base64.DEFAULT)
                )
                return params
            }
        }
        queue.add(request)
        return source.task // call validation server and retrieve firebase token
    }


    /* GOOGLE LOGIN */
    private fun googleLoginStart(){
        Log.d(TAG, "LoginActivity - googleLoginStart() called")

        // BEGIN config_signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("347398038418-2fqi70aa6dc1vea3rd91c99ea41cnkr0.apps.googleusercontent.com") // getString(R.string.default_web_client_id)
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
                    // startMainActivity()
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

        super.onActivityResult(requestCode, resultCode, data)
        // 구글 로그인 결과
        if(requestCode === RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                // 구글 로그인 성공; Firebase Auth 진행
                val account = task.getResult(ApiException::class.java)
                fbAuthGoogle(account!!)
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }


    fun startMainActivity(nickname: String, email: String){
        Log.d(TAG, "LoginActivity - startMainActivity() called")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nickname", nickname)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    // 네이버, 구글 등의 다른 로그인 API를 같이 사용하는 경우, 이 콜백  제거를 안 해주면 로그아웃 작업에서 문제 생김
    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback) //현재 액티비티 제거 시 콜백도 같이 제거
    }
}