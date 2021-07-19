package com.example.tripplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.tripplanner.R.id.btn_navi
import com.example.tripplanner.R.id.text_addr
import com.example.tripplanner.adapters.ViewPagerAdapter
import com.example.tripplanner.databinding.ActivityMainBinding
import com.example.tripplanner.fragments.ZoomOutPageTransformer
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.example.tripplanner.R.id.activity_main_layout_toolbar as activity_main_layout_toolbar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mBinding : ActivityMainBinding? = null
    private val binding get() = mBinding!!
    private val TAG : String = "로그"

    private val tabIcon = listOf(
        R.drawable.ic_baseline_calendar_today_24,
        R.drawable.ic_baseline_view_list_24,
        R.drawable.ic_baseline_wb_sunny_24
    )

    /* activity 실행시키는 부분 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(activity_main_layout_toolbar)
        setSupportActionBar(toolbar) //상단 toolbar를 app bar로 지정해주기
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // btnNavi 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24) // 홈버튼 이미지 변경

        setUpPages()

        Log.d(TAG, "MainActivity - onCreate() called")
        onClickBtnNavi()
        onClickLocation()
    }

    /* 상단 메뉴 버튼 클릭 시 Navigation tab 열림 */
    private fun onClickBtnNavi(){
        Log.d(TAG, "MainActivity - onClickBtnNavi() called")
        val btn = findViewById<ImageView>(R.id.btn_navi)
        btn.setOnClickListener{
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }
        binding.naviView.setNavigationItemSelectedListener(this) // navi 메뉴 아이템에 클릭 속성 부여
    }

    /* NavigationBar에서 각 item을 클릭했을 때 */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editprofile -> Toast.makeText(this, "프로필 수정하기", Toast.LENGTH_SHORT).show()
            R.id.itemlist -> Toast.makeText(this, "여행 물품 체크리스트", Toast.LENGTH_SHORT).show()
            R.id.friendlist -> Toast.makeText(this, "전체 친구 목록", Toast.LENGTH_SHORT).show()
            R.id.addfriend -> Toast.makeText(this, "동료 추가", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show()
            R.id.qna -> Toast.makeText(this, "qna", Toast.LENGTH_SHORT).show()
        }
        binding.layoutDrawer.closeDrawers()
        return false
    }

    /* 주소 입력 activity로 넘어가기 (intent) */
    private fun onClickLocation(){
        Log.d(TAG, "MainActivity - onClickLocation() called")
        val text = findViewById<TextView>(R.id.text_addr)
        val icon = findViewById<ImageButton>(R.id.btn_search_location)

        text.setOnClickListener {
            val intent = Intent(this, SearchAddress::class.java)
            startActivity(intent)
        }
        icon.setOnClickListener {
            val intent = Intent(this, SearchAddress::class.java)
            startActivity(intent)
        }
    }

    /* fragments, tab layout 연결 */
    private fun setUpPages(){
        Log.d(TAG, "MainActivity - setUpPages() called")

        binding.viewPager.apply{
            adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            setPageTransformer(ZoomOutPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "달력"
                1 -> tab.text = "목록"
                else -> tab.text = "날씨"
            }
            tab.setIcon(this.tabIcon[position])
        }.attach()
    }

    /* 뒤로가기 눌렀을 때 */
    override fun onBackPressed() {
        if (binding.layoutDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.layoutDrawer.closeDrawers()
            Log.d(TAG, "MainActivity - onBackPressed() called")
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

}