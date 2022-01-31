package com.kyoungae.ohnaejunggo.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kyoungae.ohnaejunggo.activity.MainActivity
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.databinding.FragmentLoginBinding
import com.kyoungae.ohnaejunggo.util.CommonUtil
import com.kyoungae.ohnaejunggo.viewmodel.LoginViewModel
import com.nhn.android.naverlogin.OAuthLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        ).apply {
            binding = this

        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isNetwork.observe(viewLifecycleOwner, Observer {
            CommonUtil.showSnackbarOfNetworkIssues(binding.parentLayout)
        })

        viewModel.succeedLogin.observe(viewLifecycleOwner, {
            if (it) {
                goToMainFragment()
            } else {
                CommonUtil.showSnackbarOfNetworkIssues(binding.parentLayout)
            }
        })

        binding.naverLoginButton.setOnClickListener {
            viewModel.initNaverLogin(mainActivity)
        }
    }

    private fun goToMainFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
        findNavController().navigate(action)
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}