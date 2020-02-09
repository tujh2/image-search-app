package com.wnp.imagesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wnp.imagesearch.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_toolbar))

        if(savedInstanceState == null) {
            navigateToFragment(MainFragment(), null)
        }
    }

    fun navigateToFragment(frag:Fragment, backStack:String?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(R.id.main_activity_fragment, frag)
        if (backStack != null)
            fragmentTransaction.addToBackStack(backStack)
        fragmentTransaction.commit()
    }
}
