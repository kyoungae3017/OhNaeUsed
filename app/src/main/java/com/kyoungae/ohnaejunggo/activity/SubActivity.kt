package com.kyoungae.ohnaejunggo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.kyoungae.ohnaejunggo.R
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.kyoungae.ohnaejunggo.databinding.ActivitySubBinding
import com.kyoungae.ohnaejunggo.util.GRAPH_ID
import kotlin.math.log


@AndroidEntryPoint
class SubActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(intent.getIntExtra(GRAPH_ID, 0))
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        setSupportActionBar(binding.viewToolbar.toolbar)
        binding.viewToolbar.toolbar.setupWithNavController(navController, appBarConfiguration)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp: ddddd")
        onBackPressed()
        return true
    }


    companion object {
        private const val TAG = "SubActivity"
    }

}