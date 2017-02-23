package ck.kbcv.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AlertDialog.Builder
import android.text.{Editable, TextWatcher}
import android.view.View
import android.widget.EditText
import ck.kbcv.fragments.FunctionEditor
import ck.kbcv.{Controller, R}


class FunctionDialog(functionEditor: FunctionEditor) extends DialogFragment {
    var inputLayout: TextInputLayout = null
    var editText: EditText = null
    var dialog: AlertDialog = null
    var function: String = null

    override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
        val activity = getActivity
        val builder = new Builder(activity, R.style.MyDialogStyle)
        val inflater = getActivity.getLayoutInflater
        inputLayout = inflater.inflate(R.layout.input_layout, null, false).asInstanceOf[TextInputLayout]
        editText = inputLayout.findViewById(R.id.edit_text).asInstanceOf[EditText]
        builder.setTitle(getString(R.string.new_fun))
            .setView(inputLayout)
            .setPositiveButton(getString(R.string.done), null)
        dialog = builder.create()

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            override def onShow(d: DialogInterface) {

                val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                button.setOnClickListener(new View.OnClickListener() {

                    def onClick(v: View): Unit = {
                        if (!validate()) {
                            editText.requestFocus()
                        } else {
                            function = editText.getText.toString
                            functionEditor.currentlySelectedSymbol = function
                            functionEditor.plusButton.setText(function)
                            dismiss()
                        }
                    }
                })
            }
        })

        dialog
    }

    override def onCancel(dialog: DialogInterface): Unit = {
        super.onCancel(dialog)
        functionEditor.resetArcSelection()
    }

    def validate(): Boolean = {
        val s = editText.getText
        if (s.length() < 1) {
            inputLayout.setError(getString(R.string.err_empty))
            false
        } else if (Controller.state.variables.contains(s.toString)) {
            inputLayout.setError(getString(R.string.err_symbol_exists))
            false
        } else {
            inputLayout.setError(null)
            true
        }

    }


    class FunctionWatcher extends TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {
        }

        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
        }

        override def afterTextChanged(s: Editable): Unit = {
            validate()
        }

    }

}

class ArityDialog(functionEditor: FunctionEditor) extends DialogFragment {
    var inputLayout: TextInputLayout = null
    var editText: EditText = null
    var dialog: AlertDialog = null
    var arity: Int = 0

    override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
        val activity = getActivity
        val builder = new Builder(activity, R.style.MyDialogStyle)
        val inflater = getActivity.getLayoutInflater
        inputLayout = inflater.inflate(R.layout.input_layout_arity, null, false).asInstanceOf[TextInputLayout]
        editText = inputLayout.findViewById(R.id.edit_text_number).asInstanceOf[EditText]
        editText.addTextChangedListener(new FunctionsWatcher)
        builder.setTitle(getString(R.string.new_fun))
            .setView(inputLayout)
            .setPositiveButton(getString(R.string.done), null)
        dialog = builder.create()

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            override def onShow(d: DialogInterface) {

                val button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                button.setOnClickListener(new View.OnClickListener() {

                    def onClick(v: View): Unit = {
                        if (!validate()) {
                            editText.requestFocus()
                        } else {
                            arity = editText.getText.toString.toInt
                            functionEditor.addNewFunction(functionEditor.currentlySelectedSymbol, arity)
                            dismiss()
                        }
                    }
                })
            }
        })

        dialog
    }

    override def onCancel(dialog: DialogInterface): Unit = {
        super.onCancel(dialog)
        functionEditor.resetArcSelection()
    }


    def validate(): Boolean = {
        val s = editText.getText.toString
        if (s.length() < 1) {
            inputLayout.setError(getString(R.string.err_empty))
            false
        } else {
            try {
                Integer.parseInt(s)
                true
            } catch {
                case ex: NumberFormatException => {
                    inputLayout.setError(getString(R.string.err_nan))
                    false
                }
            }
        }
    }

    class FunctionsWatcher extends TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {
        }

        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
        }

        override def afterTextChanged(s: Editable): Unit = {
            validate()
        }

    }
}

