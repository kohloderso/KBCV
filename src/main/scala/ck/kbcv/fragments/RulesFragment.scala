package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.RulesAdapter
import ck.kbcv.{CompletionActionListener, Controller, R}

/**
 * Created by Christina on 09.12.2015.
 */
class RulesFragment extends Fragment with ItemClickListener {
    val TAG = "RulesFragment"
    var mCompletionListener: CompletionActionListener = null
    var mRulesRV: RecyclerView = null
    var mAdapter: RulesAdapter = null
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
        val view = inflater.inflate( R.layout.rules_fragment, container, false )
        mRulesRV = view.findViewById(R.id.rvRules).asInstanceOf[RecyclerView]

        mAdapter = new RulesAdapter(Controller.state.erc._2, this)
        mRulesRV.setAdapter(mAdapter)
        mRulesRV.setLayoutManager(new LinearLayoutManager(getActivity))
        mRulesRV.setHasFixedSize(true)   // if every item has the same size, use this for better performance

        //new ItemTouchHelper(new EquationTouchHelperCallback).attachToRecyclerView(mEquationsRV)
        return view
    }

    def updateRules(): Unit = {
        val newTRS = Controller.state.erc._2
        mAdapter.updateItems(newTRS)
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


    class ActionModeCallback extends ActionMode.Callback {
        private val TAG = "ACTIONMODE"

        override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            actionMode.getMenuInflater.inflate(R.menu.selected_rule_menu, menu)
            true
        }

        override def onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            false
        }

        override def onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean = {
            val selectedPositions = mAdapter.selectedItems.clone()
            val selectedItems = mAdapter.getItems(selectedPositions)
            item.getItemId match {
                case R.id.action_compose =>
                    Log.d(TAG, "compose")
                    mCompletionListener.compose(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_collapse =>
                    Log.d(TAG, "collapse")
                    mCompletionListener.collapse(selectedItems)
                    actionMode.finish()
                    true
                case R.id.action_deduce =>
                    Log.d(TAG, "deduce")
                    mCompletionListener.deduce(selectedItems)
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