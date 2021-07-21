package com.example.tripplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.tripplanner.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var _binding : ActivitySplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val time : Long = 2000 // 2 seconds delay time
        val handler = Handler()
        handler.postDelayed({

            /* MainAct -> LoginAct로 변경하기 */
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }, time)


    }

}