package com.example.tripplanner

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException


class SessionCallback(val context : LoginActivity): ISessionCallback {
    private val TAG : String = "로그/SessionCallback"

    override fun onSessionOpened() {
        Toast.makeText(App.instance, "Successfully logged in to Kakao. Now creating or updating a Firebase User.", Toast.LENGTH_LONG).show()

        UserManagement.getInstance().me(object : MeV2ResponseCallback(){
            override fun onSuccess(result: MeV2Response?) {
                if(result != null){
                    Log.d(TAG, "세션 오픈")

                    /* return Task object that will call validation server and retrieve firebase token */
                    val accessToken = Session.getCurrentSession().tokenInfo.accessToken

                    context.getFirebaseJwt(accessToken)?.continueWithTask { task ->
                        val firebaseToken = task.result
                        val auth = FirebaseAuth.getInstance()
                        auth.signInWithCustomToken(firebaseToken!!)
                    }?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Successfully created a Firebase user")
                            val userProfile = result.profileImagePath
                            val userName = result.nickname
                            val userEmail = result.kakaoAccount.email

                            context.startMainActivity(userProfile, userName, userEmail)

                        } else {
                            Toast.makeText(App.instance,"Failed to create a Firebase user.", Toast.LENGTH_LONG).show()
                            if (task.exception != null) {
                                Log.e(TAG, task.exception.toString())
                            }
                            context.updateUI()
                        }
                    }
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e(TAG, "세션 종료")
            }

            override fun onFailure(errorResult: ErrorResult?) {
                val errorCode = errorResult?.errorCode
                val clientErrorCode = -777

                if(errorCode == clientErrorCode){
                    Log.e(TAG, "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.")
                }else{
                    Log.e(TAG, "알 수 없는 오류로 카카오로그인 실패 \n${errorResult?.errorMessage}")
                }

            }

        })
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.e(TAG, "onSessionOpenFailed ${exception?.message}")
        context.onStart() // session 연결 실패 시 LoginActivity 로 이동
    }

}