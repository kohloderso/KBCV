package ck.kbcv.dialogs

import java.io.InputStream

import android.app.{Activity, Dialog}
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog.Builder
import ck.kbcv.{Controller, OnEquationsChangedListener, OnSymbolsChangedListener, R}
import term.parser.{Parser, ParserOldTRS, ParserXmlTRS}
;

class AddDialogFragment extends DialogFragment {
    var symbolsListener: OnSymbolsChangedListener = null
    var equationsListener: OnEquationsChangedListener = null

    override def onAttach(activity: Activity) {
        super.onAttach(activity)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            symbolsListener = activity.asInstanceOf[OnSymbolsChangedListener]
            equationsListener = activity.asInstanceOf[OnEquationsChangedListener]
        } catch {
            case e: ClassCastException =>
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }

    override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
        var parser: Parser = ParserXmlTRS
        val builder = new Builder((getActivity))

        val filename = getArguments.getCharSequence("filename")
        val fileURI = getArguments.getCharSequence("uri")
        var stream: InputStream = null
        if(filename != null) {
            stream = getActivity.openFileInput(filename.toString)
        } else if(fileURI != null) {
            if(fileURI.toString.contains(".trs")) parser = ParserOldTRS
            stream = getActivity.getContentResolver.openInputStream(Uri.parse(fileURI.toString))
        }
        val es = parser.parse(stream)
        var esString = ""
        for(e <- es) {
            esString += e + "\n"
        }
        builder.setTitle(getString(R.string.add_es))
            .setMessage(esString)
            .setNeutralButton(getString(R.string.as_new), new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    Controller.setES(es, getResources.getString(R.string.ok_new_es, new Integer(es.size)))
                    symbolsListener.onFunctionsChanged()
                    symbolsListener.onVariablesChanged()
                    equationsListener.onNewEquations()
                }
            })
            .setPositiveButton(getString(R.string.add_to), new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    Controller.addES(es, getResources.getString(R.string.ok_added_es, new Integer(es.size)))
                    symbolsListener.onFunctionsChanged()
                    symbolsListener.onVariablesChanged()
                    equationsListener.onEquationsAdded()
                }
            })
        builder.create()
    }
}


