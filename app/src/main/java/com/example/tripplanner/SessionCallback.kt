package com.example.tripplanner

import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException

class SessionCallback(val context : LoginActivity): ISessionCallback {
    var userToken : String = ""
    private val userID = "USERID"
    private val TOKEN = "USERTOKEN"

    private val TAG : String = "로그"

    override fun onSessionOpened() {
        UserManagement.getInstance().me(object : MeV2ResponseCallback(){
            override fun onSuccess(result: MeV2Response?) {
                Log.i(TAG, "ID : ${result!!.id}")
                Log.i(TAG, "Email : ${result.kakaoAccount.email}")

                checkNotNull(result){
                    "Session Response Null"
                }
                //sendToServerKakao(result)
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e(TAG, "Session Call Back :: onSessionClosed ${errorResult?.errorMessage}")
            }

            override fun onFailure(errorResult: ErrorResult?) {
                Log.e(TAG, "Session Call Back :: onFailure ${errorResult?.errorMessage}")
            }

        })
    }

    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.e(TAG, "Session Call Back :: onSessionOpenFailed ${exception?.message}")
    }

}