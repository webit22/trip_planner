package com.example.tripplanner.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tripplanner.App
import com.example.tripplanner.R
import com.example.tripplanner.fragment1.ProfileViewHolder
import com.example.tripplanner.fragment1.Profiles

//data model을 담는 profileList (그릇)
class RecyclerViewAdapter : RecyclerView.Adapter<ProfileViewHolder>(){

    private val TAG: String = "로그"
    private var itemList = ArrayList<Profiles>()

    // 뷰홀더가 생성 되었을 때 (set the view to display its contents)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        // 연결할 레이아웃 설정 (recycler view item layout)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_main_rv_item, parent, false)
        return ProfileViewHolder(v)
    }

    // 뷰와 뷰홀더가 묶였을 때 (순서에 맞는 content 지정)
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        Log.d(TAG, "ProfileAdapter - onBindViewHolder() called / position: $position")
        holder.bind(this.itemList[position])

        // 클릭 설정
        holder.itemView.setOnClickListener {
            Toast.makeText(App.instance, "이름 : ${this.itemList[position].name}", Toast.LENGTH_SHORT).show()
        }
    }

    // 목록의 아이템 수
    override fun getItemCount(): Int {
        return this.itemList.size
    }

    fun submitList(profileList: ArrayList<Profiles>){
        this.itemList = profileList
    }
}