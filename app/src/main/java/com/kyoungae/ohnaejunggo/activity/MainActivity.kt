package com.kyoungae.ohnaejunggo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        signInAnonymously()
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                Log.d(TAG, "signInAnonymously: ${task.isSuccessful}")
            }
    }
    
    companion object{
        private const val TAG = "MainActivity"
    }

}