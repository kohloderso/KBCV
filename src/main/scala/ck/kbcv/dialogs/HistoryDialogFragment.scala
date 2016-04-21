package ck.kbcv.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import ck.kbcv.adapters.UndoRedoAdapter.{HistoryClickListener}
import ck.kbcv.{UpdateListener, CompletionActionListener, Controller, R}
import ck.kbcv.adapters.{UndoRedoAdapter, RulesAdapter}
import ck.kbcv.UndoRedoType._


class HistoryDialogFragment extends DialogFragment with HistoryClickListener {
    var mAdapter: UndoRedoAdapter = null
    var updateListener: UpdateListener = null

    override def onCreateDialog(savedInstanceState: Bundle): Dialog = {

        try {
            updateListener = getActivity.asInstanceOf[UpdateListener]
        } catch {
            case ex: ClassCastException => {
                throw new ClassCastException(getActivity.toString()
                    + " must implement UpdateListener ")
            }
        }

        val builder = new AlertDialog.Builder(getActivity())
        // Get the layout inflater
        val inflater = getActivity().getLayoutInflater()

        val layout = inflater.inflate(R.layout.history_dialog, null)
        val historyRV = layout.findViewById(R.id.rvHistory).asInstanceOf[RecyclerView]
        mAdapter = new UndoRedoAdapter(Controller.undoStack, Controller.redoStack, this)
        historyRV.setAdapter(mAdapter)
        historyRV.setLayoutManager(new LinearLayoutManager(getActivity))
        historyRV.setHasFixedSize(true)


        builder.setTitle("Undo/Redo History")
            .setView(layout)


        builder.create()
    }

    override def onItemClicked(position: Int): Unit = {
        val (_, t, steps) = mAdapter.getItem(position)
        t match {
            case REDO => {
                for(i <- 1 to steps) {
                    Controller.redo()
                }
            }
            case UNDO => {
                for(i <- 1 to steps) {
                    Controller.undo()
                }
            }
            case _ =>
        }
        updateListener.updateViews()
        dismiss()
    }
}
