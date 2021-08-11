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
