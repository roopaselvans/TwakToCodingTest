package com.roopasn.tawkto.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.roopasn.tawkto.R
import com.roopasn.tawkto.databinding.ActivityUserListMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserMainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mBinding: ActivityUserListMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        mBinding = ActivityUserListMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_user_list_main)
        mAppBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, mAppBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_user_list_main)
        return navController.navigateUp(mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }
}