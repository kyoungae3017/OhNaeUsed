package com.kyoungae.ohnaejunggo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast

import com.google.firebase.auth.AuthResult

import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.splashFragment, R.id.loginFragment, R.id.mainFragment),
        )
            binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}