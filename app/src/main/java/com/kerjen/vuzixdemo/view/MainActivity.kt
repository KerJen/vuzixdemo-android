package com.kerjen.vuzixdemo.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.view.fragments.IpFragment
import com.kerjen.vuzixdemo.view.fragments.ThingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openIpFragment()
    }

    fun openIpFragment() {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, IpFragment())
        }
    }

    fun openThingsFragment() {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, ThingsFragment())
        }
    }
}