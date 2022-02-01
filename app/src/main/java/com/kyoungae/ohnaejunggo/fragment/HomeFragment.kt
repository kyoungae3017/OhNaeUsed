package com.kyoungae.ohnaejunggo.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kyoungae.ohnaejunggo.activity.MainActivity
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.databinding.FragmentHomeBinding
import com.kyoungae.ohnaejunggo.databinding.FragmentMyBinding
import com.kyoungae.ohnaejunggo.databinding.FragmentSplashBinding
import com.kyoungae.ohnaejunggo.util.CommonUtil
import com.kyoungae.ohnaejunggo.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {

        }
    }

    private fun goToLoginFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToMainFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
        findNavController().navigate(action)
    }
}