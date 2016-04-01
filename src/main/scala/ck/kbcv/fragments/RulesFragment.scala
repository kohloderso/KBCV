package ck.kbcv.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view._
import ck.kbcv.adapters.EquationRuleAdapter.ItemClickListener
import ck.kbcv.adapters.{RulesAdapter, EquationsAdapter}
import ck.kbcv.{Controller, CompletionActionListener, R}
import term.parser.ParserXmlTRS

/**
 * Created by Christina on 09.12.2015.
 */
class RulesFragment extends Fragment with ItemClickListener {
    val TAG = "RulesFragment"
    var mCompletionListener: CompletionActionListener = null
    var mRulesRV: RecyclerView = null
    var mAdapter: RulesAdapter = null

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

    override def onItemClicked(position: Int): Unit = ???

    override def onItemLongClicked(position: Int): Unit = ???
}