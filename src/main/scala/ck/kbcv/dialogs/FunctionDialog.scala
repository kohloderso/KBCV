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
import android.widget.{EditText, FrameLayout}
import ck.kbcv.{Controller, OnSymbolsChangedListener, R}


class FunctionDialog extends DialogFragment {
     var inputLayout: TextInputLayout = null
    var inputArity: TextInputLayout = null
     var editText: EditText = null
    var editArity: EditText = null
     var dialog: AlertDialog = null
    var function: String = null
    var arity: Int = 0

     override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
         val activity = getActivity
         val builder = new Builder(activity)
         val inflater = getActivity.getLayoutInflater
         val inputLayoutFunction = inflater.inflate(R.layout.input_layout_function, null, false).asInstanceOf[FrameLayout]
         inputLayout = inputLayoutFunction.findViewById(R.id.text_input_layout).asInstanceOf[TextInputLayout]
         inputArity = inputLayoutFunction.findViewById(R.id.text_input_layout_number).asInstanceOf[TextInputLayout]
         editText = inputLayoutFunction.findViewById(R.id.edit_text).asInstanceOf[EditText]
         editArity = inputLayoutFunction.findViewById(R.id.edit_text_number).asInstanceOf[EditText]
         // do something about the "done" ime

         builder.setTitle(getString(R.string.new_fun))
             .setView(inputLayoutFunction)
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
                             // the user wants to submit the name
                             if(function == null) {
                                 function = editText.getText.toString
                                 inputLayout.setVisibility(View.INVISIBLE)
                                 inputArity.setVisibility(View.VISIBLE)

                             // the user wants to submit the arity
                             } else {
                                 val arity = Integer.parseInt(editArity.getText.toString)
                                 Controller.addFunction(function, arity, getString(R.string.added_fun))
                                 val symbolsListener = activity.asInstanceOf[OnSymbolsChangedListener]
                                 symbolsListener.onFunctionsChanged()
                                 dismiss()
                             }
                         }
                     }
                 })
             }
         })

         dialog
     }

     def validate(): Boolean = {
         if(function == null) {
             val s = editText.getText
             if(s.length() < 1) {
                 inputLayout.setError(getString(R.string.err_empty))
                 false
             } else if(Controller.state.variables.contains(s.toString)) {
                 inputLayout.setError(getString(R.string.err_fun_exists))
                 false
             } else {
                 inputLayout.setError(null)
                 true
             }
         } else {
             val s = editArity.getText.toString
             if(s.length() < 1) {
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
