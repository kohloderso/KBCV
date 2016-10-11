package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view.View.OnDragListener
import android.view._
import android.widget.FrameLayout
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.views.EquationEditView
import ck.kbcv.{Controller, R}
import term.reco.IE

class CreateEquationsFragment(currentIE: IE = null) extends Fragment with ItemClickListener with OnDragListener {
    val TAG = "CreateEquationsFragment"
    var equationEditView: EquationEditView = null
    var equationContainer: RecyclerView = null
    var frameLayout: FrameLayout = null
    var mAdapter: EquationsAdapter = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback

    def this() {
        this(null)
    }

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.create_equations_fragment, container, false )

        equationContainer = view.findViewById(R.id.equationsContainer).asInstanceOf[RecyclerView]
        equationEditView = view.findViewById(R.id.equation_editor).asInstanceOf[EquationEditView]
        equationEditView.setFragment(this)
        equationEditView.setEquation(currentIE)

        mAdapter = new EquationsAdapter(Controller.state.erc._1, this)
        // allow only one equation to be selected at a time, because only one can be edited at a time
        mAdapter.singleSelection = true
        equationContainer.setAdapter(mAdapter)
        equationContainer.setLayoutManager(new LinearLayoutManager(getActivity))
        equationContainer.setHasFixedSize(true)   // if every e_item has the same size, use this for better performance

        frameLayout = view.findViewById(R.id.frame_layout).asInstanceOf[FrameLayout]
        frameLayout.setOnDragListener(this)
        view
    }

    def onNewEquations(): Unit = {
        mAdapter.setNewItems(Controller.state.erc._1)
    }

    def onEquationsAdded(): Unit = {
        mAdapter.updateItems(Controller.state.erc._1)
    }

    def onEquationUpdated(ie: IE): Unit = {
        mAdapter.unmarkItem()
        mAdapter.updateInsertItem(ie)
    }

    override def onItemClicked(position: Int): Unit = {
        if(mActionMode == null) {
            mActionMode = getActivity.startActionMode(mActionModeCallback)
        }
        toggleSelection(position)
    }

    override def onItemLongClicked(position: Int): Unit = {
        if(mActionMode == null) {
            mActionMode = getActivity.startActionMode(mActionModeCallback)
        }
        toggleSelection(position)
    }

    def toggleSelection(position: Int): Unit = {
        mAdapter.toggleSelection(position)

        val count = mAdapter.selectedItems.size
        if(count == 0) {
            mActionMode.finish()
        }
//        else {
//            mActionMode.setTitle(count.toString)
//            mActionMode.invalidate()
//        } no count needed when max 1 can be selected
    }


    def addEquationFromEditor(): Unit = {
        if (equationEditView.containsDropZones()) {} // TODO: throw error or something
        else {
            val equation = equationEditView.getEquation
            if (equationEditView.index > 0) {
                // not a new equation, it's an edited equation
                val ie = (equationEditView.index, equation)
                Controller.updateEq(ie, getString(R.string.edited_eq, new Integer(ie._1)))
                onEquationUpdated(ie)
            } else {
                Controller.addEquation(equation, getString(R.string.created_eq))
                onEquationsAdded()
            }
            equationEditView.clear()
            equationEditView.index = -1
        }
    }

    override def onDrag(v: View, event: DragEvent): Boolean = {
        val action = event.getAction
        action match {
            case DragEvent.ACTION_DRAG_STARTED => //  Do nothing
            case DragEvent.ACTION_DRAG_ENTERED => if (event.getClipDescription.getLabel == "newEquation") v.setBackground(ContextCompat.getDrawable(getActivity, R.drawable.solid_border))
            case DragEvent.ACTION_DRAG_EXITED => v.setBackground(null)
            case DragEvent.ACTION_DRAG_ENDED => v.setBackground(null)
            case DragEvent.ACTION_DROP => {
                if (event.getClipDescription.getLabel == "newEquation") addEquationFromEditor()
                v.setBackground(null)
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
            val selectedPositions = mAdapter.selectedItems.clone()
            val selectedItems = mAdapter.getItems(selectedPositions)
            val id = selectedItems.firstKey
            val eq = selectedItems.get(id)
            item.getItemId match {
                case R.id.action_edit =>
                    Log.d(TAG, "edit")
                    equationEditView.setEquation((id, eq.get))
                    mAdapter.markItem(selectedPositions.head)
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    val message = getString(R.string.removed_eq, new Integer(id))
                    Controller.removeEq(id, message)
                    mAdapter.removeItem(selectedPositions.head)
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