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
import com.kyoungae.ohnaejunggo.databinding.FragmentSplashBinding
import com.kyoungae.ohnaejunggo.util.CommonUtil
import com.kyoungae.ohnaejunggo.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentSplashBinding>(
            inflater,
            R.layout.fragment_splash,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CommonUtil.showSnackbarOfNetworkIssues(binding.parentLayout)

        viewModel.loginCheck()

        viewModel.documentId.observe(viewLifecycleOwner, Observer { documentId ->
            lifecycleScope.launch {
                delay(500)
                if (documentId != null) goToMainFragment() else goToLoginFragment()
            }
        })

    }

    private fun goToLoginFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun goToMainFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
        findNavController().navigate(action)

    }

    companion object {
        private const val TAG = "SplashFragment"
    }
}