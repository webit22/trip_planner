package com.example.tripplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.tripplanner.databinding.ActivitySearchAddressBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

/* 숙소 주소 검색 */
class SearchAddress : AppCompatActivity() {

    private var _binding : ActivitySearchAddressBinding? = null
    private val binding get() = _binding!!

    private val TAG : String = "로그"

    companion object {
        const val BASE_URL = "https://dapi.kakao.com" // GET.주소 검색
        const val API_KEY = "KakaoAK 8fa175c813f7c6e8b3b38ce9fde6c39a"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //searchKeyword() 파라미터 값을 EditText에 입력한 값으로 받아오는 방법?
        searchKeyword(binding.textAddress1.toString()) // 입력한 내용 조회
        // 우편번호 조회 버튼 클릭 시 다음 주소 검색창 연결
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d(TAG, "SearchAddress - searchKeyword.Raw : ${response.raw()}")
                Log.d(TAG, "SearchAddress - searchKeyword.Body : ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.d(TAG, "SearchAddress - 통신 실패: ${t.message}")
            }
        })
    }
}