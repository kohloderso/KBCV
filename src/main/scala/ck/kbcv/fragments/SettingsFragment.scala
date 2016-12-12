package ck.kbcv.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.{EditTextPreference, Preference, PreferenceFragmentCompat}
import ck.kbcv.R
import ck.kbcv.dialogs.{NumberPickerPreference, NumberPickerPreferenceDialogFragmentCompat}


class SettingsFragment extends PreferenceFragmentCompat with OnSharedPreferenceChangeListener {
    override def onCreatePreferences(savedInstanceState: Bundle, rootKey: String): Unit = {
        addPreferencesFromResource(R.xml.preferences)
        val np = findPreference("number_rounds").asInstanceOf[NumberPickerPreference]
        np.setSummary(np.getValue + " " + getString(R.string.rounds))

        val variablePref = findPreference("variable_symbols").asInstanceOf[EditTextPreference]
        variablePref.setSummary(variablePref.getText)

        val functionPref = findPreference("function_symbols").asInstanceOf[EditTextPreference]
        functionPref.setSummary(functionPref.getText)
    }

    override def onResume(): Unit = {
        super.onResume()
        getPreferenceScreen.getSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override def onPause(): Unit = {
        super.onPause()
        getPreferenceScreen.getSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
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

    def onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) = {
        val pref = findPreference(key)
        pref match {
            case np: NumberPickerPreference => pref.setSummary(np.getValue + " " + getString(R.string.rounds))
            case et: EditTextPreference => et.setSummary(et.getText)
        }

    }
}
