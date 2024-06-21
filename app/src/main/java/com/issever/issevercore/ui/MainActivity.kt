package com.issever.issevercore.ui

import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.issever.core.base.BaseActivity
import com.issever.issevercore.R
import com.issever.issevercore.databinding.ActivityMainBinding
import com.issever.issevercore.ui.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding,MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()

    override fun initViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun init() {
        super.init()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nhfMain) as NavHostFragment
        navHostFragment.navController

    }

}