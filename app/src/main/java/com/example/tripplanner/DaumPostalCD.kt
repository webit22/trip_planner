package com.example.tripplanner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.example.tripplanner.databinding.ActivityDaumPostalCdBinding
import java.lang.StringBuilder

/* WebView - 다음 주소 검색 페이지 */
class DaumPostalCD : AppCompatActivity() {

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

            // JavaScript 이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
            // php 파일에도 "TripPlanner" 인자 값이 동일해야함
            addJavascriptInterface(AndroidBridge(), "TripPlanner")

            loadUrl("http://192.168.35.186/android_asset/daum.html") //local web server > 주소검색 파일
        }
    }

    private fun AndroidBridge(){
        @JavascriptInterface
        fun setAddress(arg1 : String, arg2 : String, arg3 : String){
            // 파라미터 : data.zonecode, data.roadAddress, data.buildingName
            // handler를 통해 javascript 이벤트 반응
            val handler = Handler()
            val result = "($arg1);$arg2;$arg3"

            handler.post(Runnable {
                intent = Intent(this, SearchAddress::class.java)
                intent.putExtra("strAddr", result)
                startActivity(intent)
            })
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