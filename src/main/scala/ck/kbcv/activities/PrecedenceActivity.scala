package ck.kbcv.activities

import android.content.ClipData
import android.os.Bundle
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view.{Menu, MotionEvent, View}
import android.widget.Button
import ck.kbcv._
import ck.kbcv.adapters.PrecedenceAdapter
import ck.kbcv.views.PrecedenceEditView
import term.Term._
import term.lpo.Precedence


class PrecedenceActivity extends NavigationDrawerActivity with TypedFindView with OnClickListener with UndoRedoActivity {
    var mRecyclerView: RecyclerView = null
    var mAdapter: PrecedenceAdapter = null
    var addButton: Button = null
    var clearButton: Button = null
    var precedenceEditView: PrecedenceEditView = null
    var functionSymbolContainer: HorizontalFlowLayout = null


    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.precedence_activity)

        mRecyclerView = findView(TR.lpoPrecContainer)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this))
        mAdapter = new PrecedenceAdapter(Controller.state.precedence)
        mRecyclerView.setAdapter(mAdapter)
        mRecyclerView.setHasFixedSize(false)

        precedenceEditView = findViewById(R.id.prec_edit_view).asInstanceOf[PrecedenceEditView]
        precedenceEditView.setPrecedenceActivity(this)

        addButton = findView(TR.addButton_prec)
        addButton.setEnabled(false)
        addButton.setOnClickListener(this)
        clearButton = findView(TR.clearButton_prec)
        clearButton.setOnClickListener(this)

        functionSymbolContainer = findViewById(R.id.functionSymbolsContainer_prec).asInstanceOf[HorizontalFlowLayout]
        onFunctionsChanged()

        if (savedInstanceState != null) {
            val (f1, f2) = savedInstanceState.getSerializable("precedence").asInstanceOf[(F, F)]
            precedenceEditView.setPrecedence(f1, f2)
        }
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.precedence_menu, menu)
        return true
    }


    override def onClick(v: View): Unit = {
        if (addButton.equals(v)) {
            if (precedenceEditView.containsDropZones()) {} // TODO: throw error or something
            else {
                var message: String = getString(R.string.error_precedence)
                val newRule = precedenceEditView.getPrecedence
                val newPrec = new Precedence(newRule, Controller.state.precedence.toList)
                if (newPrec.star.consistent) {
                    val nc = Controller.state.erc._3.toList.sortWith((t, s) => t._1 < s._1).map(_._2) // TODO understand ;-)
                    val (t, p1) = term.lpo.lpoX(nc, newPrec)

                    if (t) {
                        message = getString(R.string.ok_added_precedence, newRule._1 + " > " + newRule._2)
                        Controller.addPrecedence(p1.get, message)
                        updateViews()
                        precedenceEditView.clear()
                    } else {
                        showErrorMsg(message)
                    }
                } else {
                    showErrorMsg(message)
                }

                setAddButton()
            }
        } else if (clearButton.equals(v)) {
            precedenceEditView.clear()
            setAddButton()
        }
    }

    override def updateViews(): Unit = {
        mAdapter.updateItems(Controller.state.precedence)
    }

    def setAddButton(): Unit = {
        if (precedenceEditView.containsDropZones()) addButton.setEnabled(false)
        else addButton.setEnabled(true)
    }

    def onFunctionsChanged(): Unit = {
        val functions = Controller.state.functions
        functionSymbolContainer.removeAllViews()
        val inflater = getLayoutInflater
        for ((funName, funArity) <- functions) {
            val button = inflater.inflate(R.layout.drag_button, functionSymbolContainer, false).asInstanceOf[Button]
            button.setText(funName)
            setOnTouchFunction(button, funName)
            functionSymbolContainer.addView(button)
        }
    }

    def setOnTouchFunction(button: Button, f: F): Unit = {
        button.setOnTouchListener(new View.OnTouchListener() {
            override def onTouch(v: View, event: MotionEvent): Boolean = {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("precedence", f)
                    val shadow = new View.DragShadowBuilder(button)
                    v.startDrag(data, shadow, null, 0)
                    true
                } else {
                    false
                }
            }
        })
    }

    override def onSaveInstanceState(outState: Bundle): Unit = {
        super.onSaveInstanceState(outState)
        outState.putSerializable("precedence", precedenceEditView.getPrecedence)
    }

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_precedence)
        onFunctionsChanged()
    }

}
