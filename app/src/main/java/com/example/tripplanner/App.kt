package com.example.tripplanner

import android.app.Application
import com.bumptech.glide.Glide.init
import com.kakao.sdk.common.KakaoSdk

// 전역으로 사용가능한 context
class App : Application() {

    //singleton 사용
    companion object{
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "b6a5c39bd3995120b816c7ea7d693068")
    }
}