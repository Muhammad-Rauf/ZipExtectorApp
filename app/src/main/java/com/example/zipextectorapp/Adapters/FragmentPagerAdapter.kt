package com.example.zipfilemanager.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zipextectorapp.Fragments.*


class FragmentPagerAdapter(fragmentManager : FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImagesFragments()
            1 -> VideoFragment()
            2 -> MusicFragment()
            3 -> AppsApkFragment()
            4 -> CompressedFragment()
            5 -> DocumentsFragment()
            6 -> GeneralFragment()
            else -> ImagesFragments()
        }
    }


}