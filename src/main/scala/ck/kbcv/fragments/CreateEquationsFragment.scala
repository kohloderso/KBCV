package ck.kbcv.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.adapters.SymbolAdapter.{FunctionAdapter, VariableAdapter}
import ck.kbcv.views.EquationEditView
import ck.kbcv.{Controller, R}

class CreateEquationsFragment extends Fragment with ItemClickListener {
    val TAG = "CreateEquationsFragment"
    var functionSymbolContainer: RecyclerView = null
    var functionAdapter: FunctionAdapter = null
    var variableSymbolContainer: RecyclerView = null
    var variableAdapter: VariableAdapter = null
    var equationEditView: EquationEditView = null
    var equationContainer: RecyclerView = null
    var mAdapter: EquationsAdapter = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.create_equations_fragment, container, false )

        equationContainer = view.findViewById(R.id.equationsContainer).asInstanceOf[RecyclerView]
        functionSymbolContainer = view.findViewById(R.id.functionSymbolsContainer).asInstanceOf[RecyclerView]
        variableSymbolContainer = view.findViewById(R.id.variableSymbolContainer).asInstanceOf[RecyclerView]
        equationEditView = view.findViewById(R.id.edit_view).asInstanceOf[EquationEditView]

        val linearLayoutManager = new LinearLayoutManager(getActivity)
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        functionSymbolContainer.setLayoutManager(linearLayoutManager)
        functionAdapter = new FunctionAdapter(Controller.state.functions)
        functionSymbolContainer.setAdapter(functionAdapter)

        val linearLayoutManager2 = new LinearLayoutManager(getActivity)
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL)
        variableSymbolContainer.setLayoutManager(linearLayoutManager2)
        variableAdapter = new VariableAdapter(Controller.state.variables)
        variableSymbolContainer.setAdapter(variableAdapter)

        
        mAdapter = new EquationsAdapter(Controller.state.erc._1, this)
        // allow only one equation to be selected at a time, because only one can be edited at a time
        mAdapter.singleSelection = true
        equationContainer.setAdapter(mAdapter)
        equationContainer.setLayoutManager(new LinearLayoutManager(getActivity))
        equationContainer.setHasFixedSize(true)   // if every e_item has the same size, use this for better performance


        view
    }

    def onVariablesChanged(): Unit = {
        val variables = Controller.state.variables
        for(variable <- variables) {
            variableAdapter.addItem(variable)
        }
    }


    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        for(function <- functions) {
            functionAdapter.addItem(function)
        }
    }

    def onNewEquations(): Unit = {
        mAdapter.setNewItems(Controller.state.erc._1)
    }

    def onEquationsAdded(): Unit = {
        mAdapter.updateItems(Controller.state.erc._1)
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
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    val message = getString(R.string.removed_eq, new Integer(id))
                    Controller.removeEq(id, message)
                    mAdapter.notifyItemRemoved(selectedPositions.head)
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