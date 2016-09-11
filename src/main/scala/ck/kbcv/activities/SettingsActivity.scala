package ck.kbcv.activities

import android.os.Bundle
import ck.kbcv.fragments.SettingsFragment
import ck.kbcv.{R, TR, TypedFindView}


class SettingsActivity extends NavigationDrawerActivity with TypedFindView {
    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)

        val myToolbar = findView(TR.my_toolbar)
        setSupportActionBar(myToolbar)

        getSupportFragmentManager.beginTransaction
            .replace(R.id.frame_layout, new SettingsFragment)
            .commit()

    }



}
