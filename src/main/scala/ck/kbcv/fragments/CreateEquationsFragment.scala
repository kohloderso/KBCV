package ck.kbcv.fragments

import android.content.ClipData
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.views.EquationView
import ck.kbcv.{Controller, R}
import term.util.ES

/**
 * Created by Christina on 09.12.2015.
 */
class CreateEquationsFragment extends Fragment with ItemClickListener {
    val TAG = "CreateEquationsFragment"
    var functionSymbolContainer: LinearLayout = null
    var variableSymbolContainer: LinearLayout = null
    var equationContainer: RecyclerView = null
    var mAdapter: EquationsAdapter = null

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.create_equations_fragment, container, false )

        equationContainer = view.findViewById(R.id.equationsContainer).asInstanceOf[RecyclerView]
        functionSymbolContainer = view.findViewById(R.id.functionSymbolsContainer).asInstanceOf[LinearLayout]
        variableSymbolContainer = view.findViewById(R.id.variableSymbolContainer).asInstanceOf[LinearLayout]

        mAdapter = new EquationsAdapter(Controller.state.erc._1, this)
        equationContainer.setAdapter(mAdapter)
        equationContainer.setLayoutManager(new LinearLayoutManager(getActivity))
        equationContainer.setHasFixedSize(true)   // if every e_item has the same size, use this for better performance


        return view
    }

    def onVariablesChanged(): Unit = {
        val variables = Controller.state.variables
        variableSymbolContainer.removeAllViews()
        for(variable <- variables) {
            val b = new Button(getContext, null, android.R.attr.buttonStyleSmall)
            //b.setTextSize(getResources.getDimension(R.dimen.abc_text_size_medium_material))
            b.setText(variable)
            b.setOnTouchListener(new View.OnTouchListener() {
                override def onTouch(v: View, event: MotionEvent): Boolean = {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        val data = ClipData.newPlainText("variable", variable)
                        val shadow = new View.DragShadowBuilder(b)
                        v.startDrag(data, shadow, null, 0)
                        true
                    } else {
                        false
                    }
                }
            })
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            variableSymbolContainer.addView(b)
        }
    }


    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        val i = 0
        for(function <- functions) {
            val functionSymbol = function._1
            val arity = function._2
            val b = new Button(getContext, null, android.R.attr.buttonStyleSmall)
            b.setText(functionSymbol)
            b.setOnTouchListener(new View.OnTouchListener() {
                override def onTouch(v: View, event: MotionEvent): Boolean = {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        val data = ClipData.newPlainText("function", functionSymbol)
                        data.addItem(new ClipData.Item(arity.toString))
                        val shadow = new View.DragShadowBuilder(b)
                        v.startDrag(data, shadow, null, 0)
                        true
                    } else {
                        false
                    }

                }
            })
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
            functionSymbolContainer.addView(b)
        }
    }

    def onNewEquations(): Unit = {
        mAdapter.setNewItems(Controller.state.erc._1)
    }

    def onEquationsAdded(): Unit = {
        mAdapter.updateItems(Controller.state.erc._1)
    }

    override def onItemClicked(position: Int): Unit = {

    }

    override def onItemLongClicked(position: Int): Unit = {

    }
}