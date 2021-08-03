package com.example.tripplanner.fragment2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripplanner.adapters.RecyclerViewAdapterFrag2
import com.example.tripplanner.databinding.FragmentTriplistBinding

class TripListFragment : Fragment() {

    private var _binding: FragmentTriplistBinding? = null
    private val binding get() = _binding!!

    private val TAG : String = "로그"
    private var tripList = ArrayList<Trips>()
    private lateinit var tlAdapter: RecyclerViewAdapterFrag2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "TripListFragment - onCreate() called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment (view binding)
        _binding = FragmentTriplistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CalendarFragment - onViewCreated() called")

        try{
            var frdt : String = "2021.07.27"
            var todt : String = "2021.07.27"
            for(i in 1..10){

                val myModel = Trips("일정$i", frdt, todt) // TEST
                this.tripList.add(myModel)
            }

            // 어댑터 인스턴스 생성
            tlAdapter = RecyclerViewAdapterFrag2()
            tlAdapter.submitList(this.tripList)

            binding.rvTriplist.apply{
                // RecyclerView 방향 등 설정
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                setHasFixedSize(true)

                // adapter 장착
                adapter = tlAdapter
            }

        }catch (e : java.lang.NullPointerException) {
            Log.d(TAG, "onViewCreated()", e)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}