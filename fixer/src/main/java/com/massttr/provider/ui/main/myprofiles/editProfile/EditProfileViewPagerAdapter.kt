package com.massttr.provider.ui.main.myprofiles.editProfile

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.massttr.provider.ui.main.myprofiles.editProfile.fragment.CompanyDetailsFragment
import com.massttr.provider.ui.main.myprofiles.editProfile.fragment.PersonalDetailsFragment

class EditProfileViewPagerAdapter(@NonNull fragmentActivity: FragmentActivity?) :
    FragmentStateAdapter(
        fragmentActivity!!
    ) {

    companion object {
        private const val CARD_ITEM_SIZE = 2
    }

    override fun getItemCount(): Int = CARD_ITEM_SIZE

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            PersonalDetailsFragment()
        else
            CompanyDetailsFragment()
    }
}
