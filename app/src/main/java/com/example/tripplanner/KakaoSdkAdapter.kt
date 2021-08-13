package com.example.tripplanner

import com.kakao.sdk.common.json.KakaoDateTypeAdapter
import com.kakao.auth.*

class KakaoSdkAdapter : KakaoAdapter(){
    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig {
            App.instance?.getAppContext()
        }
    }

    override fun getSessionConfig() : ISessionConfig{
        return object : ISessionConfig{
            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_LOGIN_ALL) // 모든 로그인 방식 제공
                // Auth Type
                // KAKAO_TALK  : 카카오톡 로그인 타입
                // KAKAO_STORY : 카카오스토리 로그인 타입
                // KAKAO_ACCOUNT : 웹뷰 다이얼로그를 통한 계정연결 타입
                // KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡 로그인 타입과 함께 계정생성을 위한 버튼을 함께 제공
                // KAKAO_LOGIN_ALL : 모든 로그인 방식을 제공
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

            override fun isSecureMode(): Boolean {
                return true
            }

            override fun getApprovalType(): ApprovalType {
                return ApprovalType.INDIVIDUAL
            }

            override fun isSaveFormData(): Boolean {
                return true
            }

        }
    }
}
