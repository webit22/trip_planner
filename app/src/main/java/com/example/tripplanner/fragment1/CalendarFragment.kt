package com.example.tripplanner.fragment1

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripplanner.App
import com.example.tripplanner.MainActivity
import com.example.tripplanner.R
import com.example.tripplanner.adapters.RecyclerViewAdapterFrag1
import com.example.tripplanner.databinding.FragmentCalendarBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/* calendar 화면 구현 */
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val TAG: String = "로그"

    private var pfList = ArrayList<Profiles>()
    private lateinit var pfAdapter: RecyclerViewAdapterFrag1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment (view binding)
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CalendarFragment - onViewCreated() called")

        try {
            for (i in 1..10) {
                val myModel = Profiles(pfimage = R.drawable.profile, name = "name$i")
                this.pfList.add(myModel) //Profiles()에 입력된 내용을 arraylist에 10번 넣음
            }

            // 어댑터 인스턴스 생성
            pfAdapter = RecyclerViewAdapterFrag1()
            pfAdapter.submitList(this.pfList)

            binding.rvFragCalendar.rvProfiles.apply {
                // RecyclerView 방향 등 설정
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)

                // adapter 장착
                adapter = pfAdapter
            }

            setMemo()

            if(binding.textMemo.text == "" || binding.textMemo.text == null){
                setVisModeOne()
            }else{
                setVisModeTwo()
            }

        } catch (e: java.lang.NullPointerException) {
            Log.d(TAG, "onViewCreated()", e)
        }

    }

    /* 친구프로필 클릭 시 실행 */
    fun onProfileClicked(position: Int) {
        try {
            /* listener를 통해 item 클릭 여부를 알게됨. RecyclerViewInterface 참고. */
            // 값이 null이면 ""를 넣음. unwrapping.
            val title: String = this.pfList[position].name ?: ""

            AlertDialog.Builder(App.instance)
                .setTitle(title)
                .setMessage("$title 클릭됨")
                .setPositiveButton("OK") { _, _ ->
                    Log.d(TAG, "FriendsRV - dialog 확인 버튼 clicked")
                }
                .show()

        } catch (e: NullPointerException) {
            Log.d(TAG, "onItemClicked()", e)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setMemo() {
        // 달력 날짜 클릭 시
        binding.cv.setOnDateChangeListener { view, year, month, dayOfMonth ->
            /* months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc. */
            var strDate: String = ""

            // textDate에 해당 날짜 보임
            if (month < 9) {
                if (dayOfMonth < 10) { // 일 - 한 자리
                    strDate = "" + year + "년 " + 0 + (month + 1) + "월 " + 0 + dayOfMonth + "일"
                    binding.textDate.text = strDate
                } else { // 일 - 두 자리
                    strDate = "" + year + "년 " + 0 + (month + 1) + "월 " + dayOfMonth + "일"
                    binding.textDate.text = strDate
                }
            } else {
                if (dayOfMonth < 10) { // 일 - 한 자리
                    strDate = "" + year + "년 " + (month + 1) + "월 " + 0 + dayOfMonth + "일"
                    binding.textDate.text = strDate
                } else { // 일 - 두 자리
                    strDate = "" + year + "년 " + (month + 1) + "월 " + dayOfMonth + "일"
                    binding.textDate.text = strDate
                }
            }
            binding.edittext.setText("") // EditText에 공백값 넣기
        }
    }

    // DB에 data 읽고 쓰기, btnSave 클릭 시 실행
    override fun onStart() {
        super.onStart()
        // DB에서 데이터를 읽고 쓰기 위한 DataReference의 인스턴스
        var mDatabase = Firebase.database
        // mDatabase = FirebaseDatabase.getInstance().reference // getInstance()를 사용하여 데이터베이스의 인스턴스를 검색하고 쓰려는 위치를 참조

        // 아마 아직 여기 오류날 듯. 파베에서 text path를 추가 안해줌
        // val conditionRef = mDatabase.child("text") //.child()는 데이터가 있을 위치의 이름을 정해주는 것
        val mRef = mDatabase.getReference("text")

        mRef.addValueEventListener(object : ValueEventListener {
            // 데이터의 값이 변할 때마다 작동
            override fun onDataChange(snapshot: DataSnapshot) {
                val text = snapshot.getValue<String>()
                setVisModeTwo()

                // edittext에 입력한 내용을 textMemo에 전달
                binding.textMemo.text = text
                Log.d(TAG, "CalendarFragment - Value : $text")
                Toast.makeText(App.instance, "입력 되었습니다", Toast.LENGTH_SHORT).show()
            }

            // 에러가 날 때 작동
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "CalendarFragment - Failed to read value.", error.toException())
            }
        })

        // btnSave 클릭 시 text가 DB의 "text" path에 저장됨
        binding.btnSave.setOnClickListener{
            mRef.setValue(binding.edittext.text.toString())
        }
    }

    /* memo 입력 전 상태 */
    fun setVisModeOne(){
        binding.edittext.visibility = View.VISIBLE
        binding.btnSave.visibility = View.VISIBLE

        binding.textMemo.visibility = View.INVISIBLE
        binding.btnUpdate.visibility = View.INVISIBLE
        binding.btnDel.visibility = View.INVISIBLE
    }

    /* memo 입력 후 상태 */
    fun setVisModeTwo(){
        binding.edittext.visibility = View.INVISIBLE
        binding.btnSave.visibility = View.INVISIBLE

        binding.textMemo.visibility = View.VISIBLE
        binding.btnUpdate.visibility = View.VISIBLE
        binding.btnDel.visibility = View.VISIBLE
    }
}
