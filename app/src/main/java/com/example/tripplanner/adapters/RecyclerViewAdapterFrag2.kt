package com.example.tripplanner.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tripplanner.App
import com.example.tripplanner.R
import com.example.tripplanner.fragment1.Profiles
import com.example.tripplanner.fragment2.TripViewHolder
import com.example.tripplanner.fragment2.Trips

class RecyclerViewAdapterFrag2 : RecyclerView.Adapter<TripViewHolder>(){
    private val TAG: String = "로그"
    private var itemList = ArrayList<Trips>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        // 연결할 레이아웃 설정 (recycler view item layout)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_triplist_rv_item, parent, false)
        return TripViewHolder(v)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        Log.d(TAG, "TripListAdapter - onBindViewHolder() called / position: $position")
        holder.bind(this.itemList[position])

        // 클릭 설정
        holder.itemView.setOnClickListener {
            Toast.makeText(App.instance, "clicked : ${this.itemList[position].tripTitle}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }

    fun submitList(tl: ArrayList<Trips>){
        this.itemList = tl
    }

}