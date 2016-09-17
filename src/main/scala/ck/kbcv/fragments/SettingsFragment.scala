package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.{Preference, PreferenceFragmentCompat}
import ck.kbcv.R
import ck.kbcv.dialogs.{NumberPickerPreference, NumberPickerPreferenceDialogFragmentCompat}


class SettingsFragment extends PreferenceFragmentCompat {
    override def onCreatePreferences(savedInstanceState: Bundle, rootKey: String): Unit = {
        addPreferencesFromResource(R.xml.preferences)
    }

    override def onDisplayPreferenceDialog(preference: Preference): Unit = {
        // Try if the preference is one of our custom Preferences
        val dialogFragment: DialogFragment = preference match {
            case p: NumberPickerPreference => NumberPickerPreferenceDialogFragmentCompat.newInstance(preference.getKey)
            case _ => null
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.getFragmentManager, "android.support.v7.preference" +
                ".PreferenceFragment.DIALOG")
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

}
