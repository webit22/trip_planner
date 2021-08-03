package com.example.tripplanner.fragment1

import android.content.Context.MODE_NO_LOCALIZED_COLLATORS
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
import com.example.tripplanner.R
import com.example.tripplanner.adapters.RecyclerViewAdapterFrag1
import com.example.tripplanner.databinding.FragmentCalendarBinding
import java.io.FileInputStream
import java.io.FileOutputStream

/* calendar 화면 구현 */
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val TAG: String = "로그"

    private var pfList = ArrayList<Profiles>()
    private lateinit var pfAdapter: RecyclerViewAdapterFrag1

    var fname: String = ""
    var str: String = ""

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

            setMemo() // calendar view

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

            this.binding.textDate.visibility = View.VISIBLE
            binding.textCalendarMemo.visibility = View.VISIBLE
            binding.textSavedmemo.visibility = View.INVISIBLE// 저장된 메모 textview2 Invisible
            binding.btnCalendarSave.visibility = View.VISIBLE
            binding.btnCalendarUpdate.visibility = View.INVISIBLE // 수정 Button Invisible
            binding.btnCalendarDel.visibility = View.INVISIBLE // 삭제 Button Invisible

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

            binding.textCalendarMemo.setText("") // EditText에 공백값 넣기
            checkedDay(strDate)
        }

        // 저장 버튼 클릭 시
        binding.btnCalendarSave.setOnClickListener {
            saveMemo(fname) // saveMemo 메서드 호출
            Toast.makeText(this, fname + "데이터를 저장했습니다.", Toast.LENGTH_SHORT).show() // 토스트 메세지
            str = binding.textCalendarMemo.text.toString()

            binding.textSavedmemo.text = str // textView에 str 출력
            binding.textCalendarMemo.visibility = View.INVISIBLE
            binding.textSavedmemo.visibility = View.VISIBLE
            binding.btnCalendarSave.visibility = View.INVISIBLE // 저장 버튼 Invisible
            binding.btnCalendarUpdate.visibility = View.VISIBLE
            binding.btnCalendarDel.visibility = View.VISIBLE

        }
    }

    fun checkedDay(paraDate : String) {
        var fis: FileInputStream? = null // FileStream fis 변수 설정

        val yr = paraDate.substring(0, 3)
        val m = paraDate.substring(6, 7)
        val d = paraDate.substring(10, 11)
        var date : String = ""

        date = String.format("%s%s%s", yr, m, d)
        fname = "$date.txt" // 저장할 파일 이름 설정 : 20190120.txt

        try{
            fis = openFileInput(fname) // fname 파일 오픈

            val fileData = fis?.let { ByteArray(it.available()) } // fileData - byte 형식으로 저장
            if (fis != null) {
                fis.read(fileData) // byte 형식 파일을 읽음
                fis.close()
            }

            str = fileData?.let { String(it) }.toString() // str 변수에 fileData를 저장

            binding.textCalendarMemo.visibility = View.INVISIBLE
            binding.textSavedmemo.visibility = View.VISIBLE
            binding.textSavedmemo.text = str // textView에 str 출력
            binding.btnCalendarSave.visibility = View.INVISIBLE
            binding.btnCalendarUpdate.visibility = View.VISIBLE
            binding.btnCalendarDel.visibility = View.VISIBLE

            binding.btnCalendarUpdate.setOnClickListener { // 수정 버튼을 누를 시
                binding.textCalendarMemo.visibility = View.VISIBLE
                binding.textSavedmemo.visibility = View.INVISIBLE
                binding.textCalendarMemo.setText(str) // editText에 textView에 저장된 내용 출력

                binding.btnCalendarSave.visibility = View.VISIBLE
                binding.btnCalendarUpdate.visibility = View.INVISIBLE
                binding.btnCalendarDel.visibility = View.INVISIBLE
                binding.textSavedmemo.text = "${binding.textCalendarMemo.text}"
            }

            binding.btnCalendarDel.setOnClickListener {
                binding.textSavedmemo.visibility = View.INVISIBLE
                binding.textCalendarMemo.setText("")
                binding.textCalendarMemo.visibility = View.VISIBLE
                binding.btnCalendarSave.visibility = View.VISIBLE
                binding.btnCalendarUpdate.visibility = View.INVISIBLE
                binding.btnCalendarDel.visibility = View.INVISIBLE
                delMemo(fname)
                Toast.makeText(this, fname + "데이터를 삭제했습니다.")
            }

            if(binding.textSavedmemo.text == ""){
                binding.textSavedmemo.visibility = View.INVISIBLE
                binding.textDate.visibility = View.VISIBLE
                binding.btnCalendarSave.visibility = View.VISIBLE
                binding.btnCalendarUpdate.visibility = View.INVISIBLE
                binding.btnCalendarDel.visibility = View.INVISIBLE
                binding.textCalendarMemo.visibility = View.VISIBLE
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveMemo(para : String){
        var fos: FileOutputStream? = null

        try {
            fos = openFileOutput(para, MODE_NO_LOCALIZED_COLLATORS)
            var content: String = binding.textCalendarMemo.text.toString()
            fos?.write(content.toByteArray())
            fos?.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun delMemo(para: String) {
        var fos: FileOutputStream? = null

        try {
            fos = openFileOutput(para, MODE_NO_LOCALIZED_COLLATORS)
            var content: String = ""
            fos?.write(content.toByteArray())
            fos?.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}