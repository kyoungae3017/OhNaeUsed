package com.kyoungae.ohnaejunggo.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.activity.MainActivity
import com.kyoungae.ohnaejunggo.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> {
                    setFragment(HomeFragment())
                    setToolbarTitle(R.string.app_name)
                }
                R.id.chatting -> {
                    setFragment(ChattingFragment())
                    setToolbarTitle(R.string.chatting)
                }
                R.id.my -> {
                    setFragment(MyFragment())
                    setToolbarTitle(R.string.my)
                }
                else -> {
                    setFragment(HomeFragment())
                    setToolbarTitle(R.string.app_name)
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }

    private fun setToolbarTitle(resId: Int) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(resId)
    }

}