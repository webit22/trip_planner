package com.example.tripplanner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tripplanner.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = "로그"

    private val RC_SIGN_IN = 1000 // Google Login Result
    private var fbAuth : FirebaseAuth? = null // Firebase Auth
    private var googleSigninClient : GoogleSignInClient? = null // Google Api Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BEGIN config_signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // END

        fbAuth = FirebaseAuth.getInstance() // FB 인증을 사용할 수 있게 초기화

        googleSigninClient = GoogleSignIn.getClient(this, gso)
        //btn 클릭 시 구글 계정 인증 Activity가 보여짐
        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSigninClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        fbAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val user = fbAuth?.currentUser

                    Log.d(TAG, "LoginActivity - Login successful : ${acct.id}")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    // finish()
                }else{
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithCredential:failure", it.exception)
                }
            }
    }
}