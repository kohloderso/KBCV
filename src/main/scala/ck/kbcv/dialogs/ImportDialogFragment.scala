package ck.kbcv.dialogs

import android.app.{Activity, Dialog}
import android.content.{DialogInterface, Intent}
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog.Builder
import android.widget.Toast


class ImportDialogFragment extends DialogFragment {
    val FILE_REQUEST = 1

    override def onCreateDialog(savedInstanceState:Bundle): Dialog = {
        val builder = new Builder((getActivity))
        val filesList = getActivity.fileList()
        val items: Array[CharSequence] = filesList.map(x => x)
        builder.setTitle("Import ES")
            .setItems(items, new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    val bundle = new Bundle()
                    bundle.putCharSequence("filename", items(which))
                    val addDialog = new AddDialogFragment()
                    addDialog.setArguments(bundle)
                    addDialog.show(getActivity.getSupportFragmentManager, "AddDialog")

                }

            })
            .setNeutralButton("Import from external", new DialogInterface.OnClickListener() {
            def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                chooseFile()
            }
        })
        builder.create()
    }

    def chooseFile(): Unit = {

        val intent = new Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            getActivity.startActivityForResult(Intent.createChooser(intent, "Select a filemanager"), FILE_REQUEST)
        } catch {
            case ex: android.content.ActivityNotFoundException => {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getActivity, "Please install a File Manager.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
        // Check which request we're responding to
        if (requestCode == FILE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                val uri = data.getData
                val bundle = new Bundle()

                bundle.putCharSequence("uri", uri.toString)
                val addDialog = new AddDialogFragment()
                addDialog.setArguments(bundle)
                addDialog.show(getActivity.getSupportFragmentManager, "AddDialog")

            }
        }
    }

}