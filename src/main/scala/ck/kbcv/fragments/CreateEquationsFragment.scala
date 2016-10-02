package ck.kbcv.fragments

import android.content.ClipData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import android.widget.Button
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.{EquationsAdapter, FunctionAdapter, VariableAdapter}
import ck.kbcv.views.EquationEditView
import ck.kbcv.{Controller, HorizontalFlowLayout, R}
import term.Term._
import term.reco.IE
import term.util.Equation

class CreateEquationsFragment extends Fragment with ItemClickListener {
    val TAG = "CreateEquationsFragment"
    var functionSymbolContainer: HorizontalFlowLayout = null
    var functionAdapter: FunctionAdapter = null
    var variableSymbolContainer: HorizontalFlowLayout = null
    var variableAdapter: VariableAdapter = null
    var equationEditView: EquationEditView = null
    var equationContainer: RecyclerView = null
    var mAdapter: EquationsAdapter = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback
    var inflater: LayoutInflater = null

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.create_equations_fragment, container, false )
        this.inflater = inflater

        equationContainer = view.findViewById(R.id.equationsContainer).asInstanceOf[RecyclerView]
        functionSymbolContainer = view.findViewById(R.id.functionSymbolsContainer).asInstanceOf[HorizontalFlowLayout]
        variableSymbolContainer = view.findViewById(R.id.variableSymbolContainer).asInstanceOf[HorizontalFlowLayout]
        equationEditView = view.findViewById(R.id.edit_view).asInstanceOf[EquationEditView]

        onFunctionsChanged()
        onVariablesChanged()

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
        variableSymbolContainer.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for(variable <- variables) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(variable)
            setOnTouchVariable(button, variable)
            variableSymbolContainer.addView(button)
        }
    }


    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        val inflater = getActivity.getLayoutInflater
        for((funName, funArity) <- functions) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(funName)
            setOnTouchFunction(button, (funName, funArity))
            functionSymbolContainer.addView(button)
        }
    }

    def setOnTouchFunction(button: Button, f: (F, Int)): Unit = {
        val (function, arity) = f
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("function", function)
                    data.addItem(new ClipData.Item(arity.toString))
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }

    def setOnTouchVariable(button: Button, variable: V): Unit = {
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("variable", variable)
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
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


    override def onSaveInstanceState(outState: Bundle): Unit ={
        super.onSaveInstanceState(outState)
        outState.putSerializable("equation", equationEditView.getEquation)
        outState.putInt("index", equationEditView.index)
    }

    override def onActivityCreated(savedInstanceState:Bundle): Unit = {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {

            val equation = savedInstanceState.getSerializable("equation").asInstanceOf[Equation]
            val index = savedInstanceState.getInt("index")
            equationEditView.setEquation((index, equation))
        }
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