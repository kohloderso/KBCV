package ck.kbcv.dialogs

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.{DialogPreference, PreferenceDialogFragmentCompat}
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import ck.kbcv.R


class NumberPickerPreference(context: Context, attrs: AttributeSet) extends DialogPreference(context, attrs) {

    var mValue = 100

    override def getDialogLayoutResource: Int = R.layout.numberpicker_dialog

    def getValue: Int = mValue

    def setValue(value: Int): Unit = {
        mValue = value

        // Save to Shared Preferences
        persistInt(value)
    }

    override def onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Object): Unit = {
        // Read the value. Use the default value if it is not possible.
        val value =
            if (restorePersistedValue) getPersistedInt(mValue)
            else defaultValue.asInstanceOf[Int]
        setValue(value)
    }

    override def onGetDefaultValue(a: TypedArray, index: Int): Object = {
        // Default value from attribute. Fallback value is set to 0.
        a.getInt(index, 0).asInstanceOf[Integer]
    }
}

object NumberPickerPreferenceDialogFragmentCompat {
    def newInstance(key: String): NumberPickerPreferenceDialogFragmentCompat = {
        val fragment = new NumberPickerPreferenceDialogFragmentCompat
        val b = new Bundle(1)
        b.putString("key", key)
        fragment.setArguments(b)
        fragment
    }
}

class NumberPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    val MAX_VALUE = 1000
    val MIN_VALUE = 0
    var picker: NumberPicker = null

    override protected def onPrepareDialogBuilder(builder: AlertDialog.Builder): Unit = {
        builder.setPositiveButton("test", null)


    }

    override def onBindDialogView(view: View): Unit = {
        super.onBindDialogView(view)
        picker = view.findViewById(R.id.numberPicker).asInstanceOf[NumberPicker]

        val preference = getPreference
        val value = preference match {
            case p: NumberPickerPreference =>
                p.getValue
            case _ => 0
        }
        picker.setMinValue(MIN_VALUE)
        picker.setMaxValue(MAX_VALUE)
        picker.setValue(value)
    }

    override def onDialogClosed(positiveResult: Boolean): Unit = {
        if (positiveResult) {
            val value = picker.getValue

            getPreference match {
                case p: NumberPickerPreference =>
                    if (p.callChangeListener(value)) p.setValue(value)
                case _ =>
            }
        }
    }
}
