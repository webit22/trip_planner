package com.example.tripplanner

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.*
import android.widget.Toast
import com.example.tripplanner.databinding.ActivityDaumPostalCdBinding

/* WebView - 다음 주소 검색 페이지 */
class DaumPostalCD : AppCompatActivity(){

    private var _binding : ActivityDaumPostalCdBinding? = null
    private val binding get() = _binding!!
    private val TAG : String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDaumPostalCdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "DaumPostalCD - onCreate() called")
        openPage()
    }

    /* 주소 검색창 띄우기 */
    @SuppressLint("SetJavaScriptEnabled")
    private fun openPage(){
        /* webview 초기화 */
        binding.webView.apply{
            settings.javaScriptEnabled = true  // javascript 허용
            settings.javaScriptCanOpenWindowsAutomatically = true // javascript로 window.open 허용

            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            webViewClient.onPageFinished(this, "javascript:execDaumPostcode()")

            //javascript에서 인터페이스 호출 시 이름 "Android" 사용
            addJavascriptInterface(AndroidBridge(), "Android")
            loadUrl("http://localhost/android_asset/daum.html") //local web server > 주소검색 파일
        }

    }

    private fun AndroidBridge(){
        Log.d(TAG, "DaumPostalCD - AndroidBridge() called")
        // 주소 세팅하는 메서드
        // 자꾸 setAddress()를 호출하지 못함

        @JavascriptInterface
        fun setExtraAddress(para: String){
            binding.textJsData.text = para
        }

        @JavascriptInterface
        fun setAddress(arg: String){
            // 파라미터 : data.zonecode, data.roadAddress, data.buildingName
            // handler를 통해 javascript 이벤트 반응
            val extra : Bundle? = null
            val intent : Intent? = null

            extra?.putString("data", arg)
            if(extra != null) intent?.putExtras(extra)

            setResult(RESULT_OK, intent)
            finish()

            /*
            val handler = Handler()
            val result = "($arg1);$arg2;$arg3"
            Log.d(TAG, "DaumPostalCD - setAddress.result = $result")

            handler.post({
                intent = Intent(this, SearchAddress::class.java)
                intent.putExtra("strAddr", result)
                startActivity(intent)
                finish()
            })
            */
        }
    }

    override fun onBackPressed() {
        /* 웹사이트에서 뒤로 갈 페이지가 존재한다면 */
        if(binding.webView.canGoBack()){
            binding.webView.goBack() // 웹사이트 뒤로가기
        }else{
            super.onBackPressed()
        }
    }
}