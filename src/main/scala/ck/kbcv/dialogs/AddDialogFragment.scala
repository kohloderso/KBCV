package ck.kbcv.dialogs

import java.io.InputStream

import android.app.AlertDialog.Builder
import android.app.{Activity, Dialog}
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import ck.kbcv.{OnEquationsChangedListener, OnSymbolsChangedListener, Controller}
import term.parser.ParserXmlTRS
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
        val builder = new Builder((getActivity))

        val filename = getArguments.getCharSequence("filename")
        val fileURI = getArguments.getCharSequence("uri")
        var stream: InputStream = null
        if(filename != null) {
            stream = getActivity.openFileInput(filename.toString)
        } else if(fileURI != null) {
            stream = getActivity.getContentResolver.openInputStream(Uri.parse(fileURI.toString))
        }

        val es = ParserXmlTRS.parse(stream)
        builder.setTitle("Add ES")
            .setMessage(es.toString())
            .setNegativeButton("Use as new ES", new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    Controller.setES(es)
                    symbolsListener.onFunctionsChanged()
                    symbolsListener.onVariablesChanged()
                    equationsListener.onNewEquations(es)
                }
            })
            .setPositiveButton("Add to existing ES", new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    Controller.addES(es)
                    symbolsListener.onFunctionsChanged()
                    symbolsListener.onVariablesChanged()
                    equationsListener.onEquationsAdded(es)
                }
            })
        builder.create()
    }
}


