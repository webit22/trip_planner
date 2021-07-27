package com.example.tripplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tripplanner.databinding.ActivitySearchAddressBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

/* 숙소 주소 검색 */
class SearchAddress : AppCompatActivity() {

    private var _binding : ActivitySearchAddressBinding? = null
    private val binding get() = _binding!!

    private val TAG : String = "로그"
    private val SEARCH_ADDRESS_ACTIVITY : Int = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "SearchAddress - onCreate() called")

        binding.btnSearchPostalCD.setOnClickListener {
            val intent = Intent(this, DaumPostalCD::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, i: Intent?) {
        super.onActivityResult(requestCode, resultCode, i)
        when(requestCode){
            SEARCH_ADDRESS_ACTIVITY -> {
                if(resultCode == RESULT_OK){
                    val data = i?.extras?.getString("data")
                    if(data != null) binding.textQueryaddress1.text = data
                }
            }
        }
    }

    /*
        if(intent.hasExtra("strAddr")){ //msg key가 옳은 값을 가지고 있는지 null check
            val strAddress = intent.getStringExtra("strAddr")

            // 구분자로 string 해체
            val addr = strAddress?.split(";")
            if (addr != null) {
                binding.textQueryaddress1.text = String.format("(%s) %s %s", addr[0], addr[1], addr[2])
            }

        }else{
            Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show()
        }
        */
}