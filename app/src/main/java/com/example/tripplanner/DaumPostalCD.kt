package com.example.tripplanner

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.example.tripplanner.databinding.ActivityDaumPostalCdBinding

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
        // api 사용, webview, php, firebase랑 연동(react native?)
        binding.webView.settings.javaScriptEnabled = true // javascript 허용
        /* webview에서 새 창이 뜨지 않도록 방지 (내부적으로 창이 바뀔 수 있도록 처리) */
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.loadUrl("https://www.naver.com") // load url
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