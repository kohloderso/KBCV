package ck.kbcv.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view.View.OnDragListener
import android.view._
import android.widget.FrameLayout
import ck.kbcv._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.PrecedenceAdapter
import ck.kbcv.views.PrecedenceEditView
import term.Term._
import term.lpo.Precedence


class PrecedenceActivity extends NavigationDrawerActivity with TypedFindView with UndoRedoActivity with OnDragListener with ItemClickListener {
    var mRecyclerView: RecyclerView = null
    var mAdapter: PrecedenceAdapter = null
    var precedenceEditView: PrecedenceEditView = null
    var frameLayout: FrameLayout = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback
    var isInEditMode = false

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.precedence_activity)

        mRecyclerView = findView(TR.lpoPrecContainer)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this))
        mAdapter = new PrecedenceAdapter(Controller.state.precedence, this)
        mAdapter.singleSelection = true
        mRecyclerView.setAdapter(mAdapter)
        mRecyclerView.setHasFixedSize(false)

        precedenceEditView = findViewById(R.id.precedenceEditView).asInstanceOf[PrecedenceEditView]
        precedenceEditView.setPrecedenceActivity(this)

        if (savedInstanceState != null) {
            val (f1, f2) = savedInstanceState.getSerializable("precedence").asInstanceOf[(F, F)]
            precedenceEditView.setPrecedence(f1, f2)
        }

        frameLayout = findView(TR.frame_layout)
        frameLayout.setOnDragListener(this)
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.precedence_menu, menu)
        return true
    }

    override def onItemClicked(position: Int): Unit = {
        if(mActionMode == null) {
            mActionMode = startActionMode(mActionModeCallback)
        }
        toggleSelection(position)
    }

    override def onItemLongClicked(position: Int): Unit = {
        onItemClicked(position)
    }

    def toggleSelection(position: Int): Unit = {
        mAdapter.toggleSelection(position)

        val count = mAdapter.selectedItems.size
        if (count == 0) {
            mActionMode.finish()
        }
    }

    def addToPrecedence(): Unit = {
        val newRule = precedenceEditView.getPrecedence
        var message: String = getString(R.string.error_precedence)
        val newPrec = if(isInEditMode) new Precedence(newRule, Controller.state.precedence.toList.filter(f => f._1 != mAdapter.getMarkedItem()._1 || f._2 != mAdapter.getMarkedItem()._2))
        else new Precedence(newRule, Controller.state.precedence.toList)
        
        if (newPrec.star.consistent) {
            val nc = Controller.state.erc._3.toList.sortWith((t, s) => t._1 < s._1).map(_._2) // TODO understand ;-)
            val (t, p1) = term.lpo.lpoX(nc, newPrec)

            if (t) {
                message = if(isInEditMode) getString(R.string.edited_prec)
                else getString(R.string.ok_added_precedence, newRule._1 + " > " + newRule._2)
                Controller.changePrecedence(p1.get, message)
                updateViews()
                precedenceEditView.clear()
                mAdapter.unmarkItem()
                isInEditMode = false
            } else {
                showErrorMsg(message)
            }
        } else {
            showErrorMsg(message)
        }
    }

    def removeFromPrecedence(rule: (F, F)): Unit = {
        var message: String = getString(R.string.error_remove_precedence)
        val newPrec = new Precedence(Controller.state.precedence.toList.filter(t => t!= rule))
        if (newPrec.star.consistent) {
            val nc = Controller.state.erc._3.toList.sortWith((t, s) => t._1 < s._1).map(_._2) // TODO understand ;-)
            val (t, p1) = term.lpo.lpoX(nc, newPrec)

            if (t) {
                message = getString(R.string.removed_precedence)
                Controller.changePrecedence(p1.get, message)
                mAdapter.removeItem(rule)
                showSuccessMsg(message)
            } else {
                showErrorMsg(message)
            }
        } else {
            showErrorMsg(message)
        }
    }

    override def updateViews(): Unit = {
        mAdapter.updateItems(Controller.state.precedence)
    }

    override def onSaveInstanceState(outState: Bundle): Unit = {
        super.onSaveInstanceState(outState)
        outState.putSerializable("precedence", precedenceEditView.getPrecedence)
    }

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_precedence)
        precedenceEditView.onFunctionsChanged()
    }

    override def onDrag(v: View, event: DragEvent): Boolean = {
        val action = event.getAction
        action match {
            case DragEvent.ACTION_DRAG_STARTED => //  Do nothing
            case DragEvent.ACTION_DRAG_ENTERED => if (event.getClipDescription.getLabel == "newPrec") v.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.solid_border))
            case DragEvent.ACTION_DRAG_EXITED => v.setBackgroundDrawable(null)
            case DragEvent.ACTION_DRAG_ENDED => v.setBackgroundDrawable(null)
            case DragEvent.ACTION_DROP => {
                if (event.getClipDescription.getLabel == "newPrec") addToPrecedence()
                v.setBackgroundDrawable(null)
            }
            case _ =>
        }
        true
    }

    class ActionModeCallback extends ActionMode.Callback {
        private val TAG = "ACTIONMODE"

        override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            actionMode.getMenuInflater.inflate(R.menu.edit_equation_menu, menu)
            true
        }

        override def onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            false
        }

        override def onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean = {
            val selectedPosition = mAdapter.selectedItems.head
            val selectedPrec = mAdapter.getItem(selectedPosition)
            item.getItemId match {
                case R.id.action_edit =>
                    Log.d(TAG, "edit")
                    isInEditMode = true
                    precedenceEditView.setPrecedence(selectedPrec._1, selectedPrec._2)
                    precedenceEditView.setAddButton("save")
                    mAdapter.markItem(selectedPosition)
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    removeFromPrecedence(selectedPrec)
                    actionMode.finish()
                    true
                case _ => false
            }
        }

        override def onDestroyActionMode(actionMode: ActionMode): Unit = {
            mAdapter.clearSelection()
            mActionMode = null
        }

    }
}
