package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.activities.CompletionActivity
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.RulesAdapter
import ck.kbcv.views.TermPairView.OnDropListener
import ck.kbcv.{CompletionActionListener, Controller, R}
import term.reco
import term.reco.{ERCH, ITRS, Simp}


class RulesFragment extends Fragment with ItemClickListener with OnDropListener {
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

    /**
     *
     * @param idRule
     * @param idDrop
     * @param leftRight 0 for left, 1 for right
     */
    override def onRuleDropped(idRule: Int, idDrop: Int, leftRight: Int): Unit = {
        val erch = Controller.state.erc
        val itrs = new ITRS() + ((idRule, erch._2.get(idRule).get)) + ((idDrop, erch._2.get(idDrop).get))
        val erch_onlyRule = new ERCH(erch._1, itrs, itrs, erch._4)
        val simp = leftRight match {
            case 0 => Simp.RuL
            case 1 => Simp.RuR
        }
        var nerch = reco.concurrentSimpToNF(0, Controller.state.depth, Controller.emptyS, Controller.emptyTI, Controller.emptyI + idDrop, erch_onlyRule, simp)

        if(nerch._2 != erch_onlyRule._2) {
            var message: String = null
            if(simp == Simp.RuL) {
               nerch = new ERCH(nerch._1, erch._2 - idDrop, erch._3 - idDrop, nerch._4)
                message = getString(R.string.ok_collapse)
            } else if(simp == Simp.RuR) {
                nerch = new ERCH(nerch._1, (erch._2 - idDrop) ++ nerch._2, (erch._3 - idDrop) ++ nerch._3, nerch._4)
                message = getString(R.string.ok_compose)
            }

            Controller.builder.
                withErch(nerch).
                withMessage(message).
                updateState()
            mCompletionListener.asInstanceOf[CompletionActivity].updateViews() // TODO put this method in CompletionActivity
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