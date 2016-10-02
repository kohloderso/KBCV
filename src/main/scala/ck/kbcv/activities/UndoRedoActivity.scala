package ck.kbcv.activities

import android.app.Activity
import android.graphics.{Color, PorterDuff}
import android.support.design.widget.Snackbar
import android.view.{Menu, MenuItem, View}
import ck.kbcv.{Controller, R}


trait UndoRedoActivity extends Activity {

    def showErrorMsg(message: String): Unit = {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
    }

    def showSuccessMsg(message: String): Unit = {
        val onClickListener = new View.OnClickListener {
            override def onClick(v: View) {
                Controller.undo()
                updateViews()
            }
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("undo", onClickListener)
            .show()
    }

    override def onPrepareOptionsMenu(menu: Menu): Boolean = {
        val undoEnabled = Controller.undoable(1)
        val undoItem = menu.findItem(R.id.undo)
        val resIcon = getResources().getDrawable(R.drawable.ic_undo_white_24dp)
        if (!undoEnabled) {
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }
        undoItem.setEnabled(undoEnabled)
        undoItem.setIcon(resIcon)

        val redoEnabled = Controller.redoable(1)
        val redoItem = menu.findItem(R.id.redo)
        val resIcon2 = getResources().getDrawable(R.drawable.ic_redo_white_24dp)
        if (!redoEnabled) {
            resIcon2.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }
        redoItem.setEnabled(redoEnabled)
        redoItem.setIcon(resIcon2)
        true
    }

    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.undo => {
                Controller.undo()
                updateViews()
                true
            }
            case R.id.redo => {
                Controller.redo()
                updateViews()
                true
            }
            case _ => super.onOptionsItemSelected(item)
        }
    }

    def updateViews(): Unit


}
