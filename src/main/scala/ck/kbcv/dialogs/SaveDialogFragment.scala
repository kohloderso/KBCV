package ck.kbcv.dialogs

import java.io.{File, FileOutputStream}

import android.app.{Activity, Dialog}
import android.content.{Context, DialogInterface}
import android.os.{Bundle, Environment}
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog.Builder
import android.util.Log
import android.widget.{EditText, Toast}
import ck.kbcv.Controller
import term.parser.{ParserOldTRS, ParserXmlTRS}


class SaveDialogFragment(saveRules: Boolean = false) extends DialogFragment {
    val TAG = "SAVE_DIALOG"

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
                    if (!saveRules) saveESxml(filename, activity)
                    else saveTRS(filename + ".trs", activity)
                }
            })
        builder.create()
    }

    def saveESxml(filename: String, activity: Activity): Unit = {
        val xmlTRS = ParserXmlTRS.eqToXML(Nil ++ Controller.state.e0.values)
        try {
            val outputStream = activity.openFileOutput(filename, Context.MODE_WORLD_READABLE)
            outputStream.write(xmlTRS.toString.getBytes())
            outputStream.close()
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }

    def saveTRS(filename: String, activity: Activity): Unit = {
        val path = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "KBCV")
        if (!path.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        val file = new File(path, filename)
        try {
            val outputStream = new FileOutputStream(file)
            outputStream.write(ParserOldTRS.toOldString(Controller.state.erc._2, false).getBytes)
            outputStream.close()
            Toast.makeText(activity, "Saved Rules as " + filename + " in " + path.getPath, Toast.LENGTH_LONG)
                .show()
        } catch {
            case e: Exception => e.printStackTrace()
        }

    }

}