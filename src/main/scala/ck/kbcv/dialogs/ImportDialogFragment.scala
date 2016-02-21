package ck.kbcv.dialogs

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment


class ImportDialogFragment extends DialogFragment {
    override def onCreateDialog(savedInstanceState:Bundle): Dialog = {
        val builder = new Builder((getActivity))
        val filesList = getActivity.fileList()
        val items: Array[CharSequence] = filesList.map(x => x)
        builder.setTitle("Import TRS")
            .setItems(items, new DialogInterface.OnClickListener() {
                def onClick(dialogInterface: DialogInterface, which: Int): Unit = {
                    val bundle = new Bundle()
                    bundle.putCharSequence("filename", items(which))
                    val addDialog = new AddDialogFragment()
                    addDialog.setArguments(bundle)
                    addDialog.show(getActivity.getSupportFragmentManager, "AddDialog")

                }

            })
        builder.create()
    }

}