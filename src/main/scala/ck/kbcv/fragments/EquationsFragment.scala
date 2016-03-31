package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.adapters.EquationsAdapter.ItemClickListener
import ck.kbcv.{CompletionActionListener, OnSymbolsChangedListener, Controller, R}
import term.parser.ParserXmlTRS


class EquationsFragment extends Fragment with ItemClickListener {
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

        // remove this after testing!!
        if(Controller.state.equations.isEmpty) {
            val stream = getActivity.openFileInput("new")
            val es = ParserXmlTRS.parse(stream)
            Controller.setES(es)
        }
        mAdapter = new EquationsAdapter(Controller.state.equations.toBuffer, this)
        mEquationsRV.setAdapter(mAdapter)
        mEquationsRV.setLayoutManager(new LinearLayoutManager(getActivity))
        mEquationsRV.setHasFixedSize(true)   // if every item has the same size, use this for better performance

        new ItemTouchHelper(new EquationTouchHelperCallback).attachToRecyclerView(mEquationsRV)
        return view
    }

    def onLeftSwipe(position: Int): Unit = {
        mCompletionListener.orientLR(mAdapter.getItem(position))
    }

    def onRightSwipe(position: Int): Unit = {
        mCompletionListener.orientRL(mAdapter.getItem(position))
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

    class EquationTouchHelperCallback() extends ItemTouchHelper.Callback {

        override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
            val position = viewHolder.getAdapterPosition
            //if(mAdapter.isSelected(position)) return 0 TODO: allow swiping when it's selected?
            return ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT )
        }

        override def isLongPressDragEnabled: Boolean = false

        override def isItemViewSwipeEnabled: Boolean = true

        override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = ???

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
            item.getItemId match {
                case R.id.action_orientLR =>
                    Log.d(TAG, "orientLR")
                    val selectedPositions = mAdapter.selectedItems
                    for(position <- selectedPositions) {
                        val equation = mAdapter.getItem(position)
                        mCompletionListener.orientLR(equation)
                    }
                    actionMode.finish()
                    true
                case R.id.action_orientRL =>
                    Log.d(TAG, "orientRL")
                    // TODO
                    actionMode.finish()
                    true
                case R.id.action_simplify =>
                    Log.d(TAG, "simplify")
                    // TODO
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    // TODO
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