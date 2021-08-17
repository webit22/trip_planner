package com.example.tripplanner.fragment1

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tripplanner.App
import com.example.tripplanner.R

//커스텀 뷰홀더
// viewholder - 이미지, 이름 item들을 adapter에 연결해주는 역할

class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val TAG: String = "로그"

    private val pfImageView : ImageView
    private val nameTextView: TextView

    // 기본 생성자
    init {
        Log.d(TAG, "ProfileViewHolder - init() called")

        pfImageView = itemView.findViewById(R.id.friendProfile)
        nameTextView = itemView.findViewById(R.id.friendName)
    }

    //데이터와 뷰를 묶는다.
    fun bind(profile: Profiles){

        Log.d(TAG, "ProfileViewHolder - bind() called")

        // textview와 실제 textview를 묶는다
        nameTextView.text = profile.name

        //imageview와 실제 imageview를 묶는다.
        App.instance?.let {
            Glide
                .with(it) // 자기 자신의 context 가져옴
                .load(profile.pfimage)
                .placeholder(R.drawable.profile)
                .into(pfImageView)
        }
    }


//    // listener를 통해 item이 클릭되었다는걸 FriendsRV()에게 알려줌
//    override fun onClick(v: View?) {
//        Log.d(TAG, "ProfileViewHolder - onClick() called")
//        this.RVInterface?.onItemClicked(absoluteAdapterPosition)
//
//    }
}