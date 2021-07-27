package com.example.tripplanner.fragment2

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tripplanner.R

// custom view holder
class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val TAG: String = "로그"

    private val titleTextView : TextView
    private val frdtTextView : TextView
    private val todtTextView : TextView

    init {
        Log.d(TAG, "TripViewHolder - init() called")

        titleTextView = itemView.findViewById(R.id.text_triptitle)
        frdtTextView = itemView.findViewById(R.id.text_frdt)
        todtTextView = itemView.findViewById(R.id.text_todt)
    }

    //데이터와 뷰를 묶는다.
    fun bind(trip: Trips){

        Log.d(TAG, "TripViewHolder - bind() called")

        // textview와 실제 textview를 묶는다
        titleTextView.text = trip.tripTitle
        frdtTextView.text = trip.frDt
        todtTextView.text = trip.toDt
    }
}