package com.example.tripplanner.fragment1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripplanner.*
import com.example.tripplanner.R
import com.example.tripplanner.adapters.RecyclerViewAdapterFrag1
import com.example.tripplanner.databinding.FragmentCalendarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import java.lang.NullPointerException
import java.time.LocalDate

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

            /* Friend List */
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

            /* CalendarView + Memo */
            setCalendarView()

        } catch (e: NullPointerException) {
            Log.w(TAG, "onViewCreated()", e)
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "CalendarFragment - onDestroyView() called")
        _binding = null
        super.onDestroyView()
    }

    private fun setCalendarView(){
        Log.d(TAG, "CalendarFragment - setCalendarView() called")

        val dateToday: LocalDate = LocalDate.now()
        binding.textDate.text = dateToday.toString() // .format("yyyy년 MM월 dd일")

        // 달력 날짜 클릭 시
        binding.cv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            /* months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc. */
            val strDate: String
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
            getData(strDate) // DB에서 memo 값 읽어오기
        }
    }

    /* DB - DB에서 Data 불러와서 읽음 */
    private fun getData(strDate: String){
        val date: String = strDate.substring(0,4) + strDate.substring(6,8) + strDate.substring(10,12)
        val mDB = Firebase.database // main DB instance
        var mRef: DatabaseReference

        try{
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)

                }else if (user != null){
                    Log.i(TAG, "사용자 정보 요청 성공\n  회원번호: ${user.id}")
                    mRef = mDB.getReference("Users/${user.id}/${date}")

                    mRef.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val memo = snapshot.child("memo").value

                                Log.d(TAG, "date: $date memo: $memo")

                                binding.textMemo.text = memo.toString() // set data to TextView
                                setVisModeTwo()

                                // btnUpdate 클릭 시 DB에 값 수정
                                binding.btnUpdate.setOnClickListener {
                                    updateData()
                                }
                                // btnDel 클릭 시 DB에 값 저장
                                binding.btnDel.setOnClickListener {
                                    deleteData()
                                }
                            }else{
                                Toast.makeText(App.instance, "저장된 내용이 없음", Toast.LENGTH_SHORT).show()
                                setVisModeOne()

                                // btnSave 클릭 시 DB에 값 저장
                                binding.btnSave.setOnClickListener{
                                    saveData()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // data 읽어오기에 실패했을 때
                            Log.d(TAG, "Failed to read data")
                        }
                    })
                }
            }
        }catch(e: NullPointerException){
        }
    }

    /* DB - Data 쓰고 DB에 저장 */
    private fun saveData() {
        Log.d(TAG, "CalendarFragment - saveData() called")

        val temp: String = binding.textDate.text.toString()
        val date: String = temp.substring(0,4) + temp.substring(6,8) + temp.substring(10,12)
        val memo: String = binding.edittext.text.toString()

        val mDB = Firebase.database // main DB instance
        val userDataset = User(date, memo)
        var mRef : DatabaseReference


        try{
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)

                }else if (user != null){
                    Log.i(TAG, "사용자 정보 요청 성공\n  회원번호: ${user.id}")

                    mRef = mDB.getReference("Users/${user.id}/${date}")

                    if(date.isNotEmpty() && memo.isNotEmpty()){
                        /*
                        mRef.setValue(userDataset).addOnCompleteListener {
                            binding.textMemo.text = memo
                            binding.edittext.text.clear()

                            Toast.makeText(App.instance, "Successfully saved", Toast.LENGTH_SHORT).show()
                            setVisModeTwo()

                        }.addOnFailureListener {
                            Toast.makeText(App.instance, "Failed", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "저장 실패")
                        }
                         */
                    } else if(memo.isNullOrEmpty()){
                        Toast.makeText(App.instance, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Memo Empty. Request Denied.")
                    }
                }
            }
        }catch (e: NullPointerException){
            Log.d(TAG, "NullPointerException", e)
        }
    }

    /* DB - Data 수정하고 DB에 저장 */
    private fun updateData(){
        /*
        * 1. editText.text = textMemo.text
        * 2. Mode1 호출
        * 3. saveData() 호출
        * */
    }

    /* DB - DB에서 Data 삭제 */
    private fun deleteData(){
        /*
        * 1. Dialog 띄움
        * 2. 확인 버튼 클릭 : DB에서 날짜 조회 > date, memo 삭제; Log.d(successfully deleted(date:$date))
        * 3. 취소 버튼 클릭 : nothing occurs. Log.d(canceled)
        * 4. Mode1 호출
        * */
    }

    /* memo 입력 전 상태 */
    private fun setVisModeOne(){
        Log.d(TAG, "CalendarFragment - Mode1 called")

        binding.edittext.visibility = View.VISIBLE
        binding.btnSave.visibility = View.VISIBLE

        binding.textMemo.visibility = View.GONE
        binding.btnUpdate.visibility = View.GONE
        binding.btnDel.visibility = View.GONE
    }

    /* memo 입력 후 상태 */
    private fun setVisModeTwo(){
        Log.d(TAG, "CalendarFragment - Mode2 called")

        binding.edittext.visibility = View.GONE
        binding.btnSave.visibility = View.GONE

        binding.textMemo.visibility = View.VISIBLE
        binding.btnUpdate.visibility = View.VISIBLE
        binding.btnDel.visibility = View.VISIBLE
    }

}
