package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.views.TermPairView.OnDropListener
import ck.kbcv.{CompletionActionListener, Controller, R}
import term.reco
import term.reco.{ERCH, IS, ITRS, Simp}

class EquationsFragment extends Fragment with ItemClickListener with OnDropListener {
    val TAG = "EquationsFragment"
    var mCompletionListener: CompletionActionListener = null
    var mEquationsRV: RecyclerView = null
    var mAdapter: EquationsAdapter = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback


    override def onAttach(context: Context): Unit = {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCompletionListener = getActivity.asInstanceOf[CompletionActionListener]
        } catch {
            case ex: ClassCastException => {
                throw new ClassCastException(getActivity.toString()
                    + " must implement CompletionActionListener ")
            }
        }
    }

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equations_fragment, container, false )
        mEquationsRV = view.findViewById(R.id.rvEquations).asInstanceOf[RecyclerView]

        mAdapter = new EquationsAdapter(Controller.state.erc._1, this)
        mEquationsRV.setAdapter(mAdapter)
        mEquationsRV.setLayoutManager(new LinearLayoutManager(getActivity))
        mEquationsRV.setHasFixedSize(false)   // if every e_item has the same size, use this for better performance

        new ItemTouchHelper(new EquationTouchHelperCallback).attachToRecyclerView(mEquationsRV)
        return view
    }

    def updateEquations(): Unit = {
        val newIES = Controller.state.erc._1
        mAdapter.updateItems(newIES)
    }

    def onLeftSwipe(position: Int): Unit = {
        val success = mCompletionListener.orientRL(new IS + mAdapter.getItem(position))
        if(!success) mAdapter.notifyItemChanged(position)
        else mAdapter.moveSelection(position)
    }

    def onRightSwipe(position: Int): Unit = {
        val success = mCompletionListener.orientLR(new IS + mAdapter.getItem(position))
        if(!success) mAdapter.notifyItemChanged(position)
        else mAdapter.moveSelection(position)
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
        } else {
            mActionMode.setTitle(count.toString)
            mActionMode.invalidate()
        }
    }

    /**
     *
     * @param idRule
     * @param idDrop
     * @param leftRight 0 for left, 1 for right
     */
    override def onRuleDropped(idRule: Int, idDrop: Int, leftRight: Int): Unit = {
        val erch = Controller.state.erc
        val itrs = new ITRS() + ((idRule, erch._2.get(idRule).get))
        val erch_onlyRule = new ERCH(erch._1, itrs, itrs, erch._4)
        val simp = leftRight match {
            case 0 => Simp.EqL
            case 1 => Simp.EqR
        }
        val nerch = reco.concurrentSimpToNF(0, Controller.state.depth, Controller.emptyS, Controller.emptyTI, Controller.emptyI + idDrop, erch_onlyRule, simp)

        if(nerch._1 != erch._1) {
            val message = getString(R.string.ok_simplify)
            Controller.builder.
                withErch(new ERCH(nerch._1, erch._2, erch._3, nerch._4)).
                withMessage(message).
                updateState()
            updateEquations()
        }
    }

    class EquationTouchHelperCallback() extends ItemTouchHelper.Callback {

        override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
            val position = viewHolder.getAdapterPosition
            if (mAdapter.isSelected(position)) return 0 // TODO when something is selected don't allow swiping and dragging?
            val dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            val swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }

        override def isLongPressDragEnabled: Boolean = false

        override def isItemViewSwipeEnabled: Boolean = true

        override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = {
            true
        }

        override def onSwiped(viewHolder: ViewHolder, direction: Int): Unit = {
            direction match {
                case ItemTouchHelper.LEFT => onLeftSwipe(viewHolder.getAdapterPosition)
                case ItemTouchHelper.RIGHT => onRightSwipe(viewHolder.getAdapterPosition)
                case _ =>
            }
        }
    }

    class ActionModeCallback extends ActionMode.Callback {
        private val TAG = "ACTIONMODE"

        override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            actionMode.getMenuInflater.inflate(R.menu.selected_equation_menu, menu)
            true
        }

        override def onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            false
        }

        override def onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean = {
            val selectedPositions = mAdapter.selectedItems.clone()
            val selectedItems = mAdapter.getItems(selectedPositions)
            item.getItemId match {
                case R.id.action_orientLR =>
                    Log.d(TAG, "orientLR")
                    mCompletionListener.orientLR(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_orientRL =>
                    Log.d(TAG, "orientRL")
                    mCompletionListener.orientRL(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_simplify =>
                    Log.d(TAG, "simplify")
                    mCompletionListener.simplify(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    mCompletionListener.delete(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_select_all =>
                    mAdapter.selectAll()
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