package ck.kbcv.dialogs

import android.app.Dialog
import android.content.{Context, DialogInterface}
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog.Builder
import android.widget.EditText
import ck.kbcv.Controller
import term.parser.ParserXmlTRS


class SaveDialogFragment extends DialogFragment {
    override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
        val activity = getActivity
        val builder = new Builder((activity))
        val edittext = new EditText(activity)
        edittext.setPadding(20, 20, 20, 20)
        edittext.setHint("name")

        builder.setTitle("Save TRS")
            .setView(edittext)
            .setPositiveButton("save", new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    val filename = edittext.getText.toString
                    val xmlTRS = ParserXmlTRS.eqToXML(Nil ++ Controller.state.e0.values)
                    try {
                        val outputStream = activity.openFileOutput(filename, Context.MODE_WORLD_READABLE)
                        outputStream.write(xmlTRS.toString.getBytes())
                        outputStream.close()
                    } catch {
                        case e: Exception=> e.printStackTrace()
                    }
                }
            })
        builder.create()
    }

}