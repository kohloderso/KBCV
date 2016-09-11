package ck.kbcv.fragments

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import ck.kbcv.R


class SettingsFragment extends PreferenceFragmentCompat {
    override def onCreatePreferences(savedInstanceState: Bundle, rootKey: String): Unit = {
        addPreferencesFromResource(R.xml.preferences)
    }

}
