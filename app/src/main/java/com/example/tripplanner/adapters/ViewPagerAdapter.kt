package com.example.tripplanner.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tripplanner.fragment1.CalendarFragment
import com.example.tripplanner.fragment2.TripListFragment
import com.example.tripplanner.fragments.WeatherFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val NUM_PAGES = 3
    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CalendarFragment()
            1 -> TripListFragment()
            else -> WeatherFragment()
        }
    }
}