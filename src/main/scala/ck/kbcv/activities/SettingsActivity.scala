package ck.kbcv.activities

import android.os.Bundle
import ck.kbcv.fragments.SettingsFragment
import ck.kbcv.{R, TypedFindView}


class SettingsActivity extends NavigationDrawerActivity with TypedFindView {
    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        getSupportFragmentManager.beginTransaction
            .replace(R.id.frame_layout, new SettingsFragment)
            .commit()

    }

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_settings)
    }



}
