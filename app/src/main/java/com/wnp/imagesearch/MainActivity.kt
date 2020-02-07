package com.wnp.imagesearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
