package com.example.tripplanner.fragments

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
import com.example.tripplanner.fragment1.Profiles

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment (view binding)
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CalendarFragment - onViewCreated() called")

        try{
            for(i in 1..10){
                val myModel = Profiles(pfimage = R.drawable.profile, name = "name$i")
                this.pfList.add(myModel) //Profiles()에 입력된 내용을 arraylist에 10번 넣음
            }

            // 어댑터 인스턴스 생성
            pfAdapter = RecyclerViewAdapterFrag1()
            pfAdapter.submitList(this.pfList)

            binding.rvFragCalendar.rvProfiles.apply{
                // RecyclerView 방향 등 설정
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
                setHasFixedSize(true)

                // adapter 장착
                adapter = pfAdapter
            }

            // calendar view
            binding.cv.setOnDateChangeListener { view, year, month, dayOfMonth ->
                /* months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc. */

                val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }

        }catch (e : java.lang.NullPointerException) {
            Log.d(TAG, "onViewCreated()", e)
        }

    }

    /* 친구프로필 클릭 시 실행 */
    fun onProfileClicked(position: Int){
        try{
            /* listener를 통해 item 클릭 여부를 알게됨. RecyclerViewInterface 참고. */
            // 값이 null이면 ""를 넣음. unwrapping.
            val title: String = this.pfList[position].name ?: ""

            AlertDialog.Builder(App.instance)
                .setTitle(title)
                .setMessage("$title 클릭됨")
                .setPositiveButton("OK"){ _, _ ->
                    Log.d(TAG, "FriendsRV - dialog 확인 버튼 clicked")
                }
                .show()

        }catch (e : NullPointerException) {
            Log.d(TAG, "onItemClicked()", e)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}