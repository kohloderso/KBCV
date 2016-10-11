package ck.kbcv.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.View.OnDragListener
import android.view.{DragEvent, Menu, View}
import android.widget.FrameLayout
import ck.kbcv._
import ck.kbcv.adapters.PrecedenceAdapter
import ck.kbcv.views.PrecedenceEditView
import term.Term._
import term.lpo.Precedence


class PrecedenceActivity extends NavigationDrawerActivity with TypedFindView with UndoRedoActivity with OnDragListener {
    var mRecyclerView: RecyclerView = null
    var mAdapter: PrecedenceAdapter = null
    var precedenceEditView: PrecedenceEditView = null
    var frameLayout: FrameLayout = null

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.precedence_activity)

        mRecyclerView = findView(TR.lpoPrecContainer)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this))
        mAdapter = new PrecedenceAdapter(Controller.state.precedence)
        mRecyclerView.setAdapter(mAdapter)
        mRecyclerView.setHasFixedSize(false)

        precedenceEditView = findViewById(R.id.precedenceEditView).asInstanceOf[PrecedenceEditView]
        precedenceEditView.setPrecedenceActivity(this)

        if (savedInstanceState != null) {
            val (f1, f2) = savedInstanceState.getSerializable("precedence").asInstanceOf[(F, F)]
            precedenceEditView.setPrecedence(f1, f2)
        }

        frameLayout = findView(TR.frame_layout)
        frameLayout.setOnDragListener(this)
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.precedence_menu, menu)
        return true
    }

    def addToPrecedence(): Unit = {
        val newRule = precedenceEditView.getPrecedence
        var message: String = getString(R.string.error_precedence)
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
    }

    override def updateViews(): Unit = {
        mAdapter.updateItems(Controller.state.precedence)
    }

    override def onSaveInstanceState(outState: Bundle): Unit = {
        super.onSaveInstanceState(outState)
        outState.putSerializable("precedence", precedenceEditView.getPrecedence)
    }

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_precedence)
        precedenceEditView.onFunctionsChanged()
    }

    override def onDrag(v: View, event: DragEvent): Boolean = {
        val action = event.getAction
        action match {
            case DragEvent.ACTION_DRAG_STARTED => //  Do nothing
            case DragEvent.ACTION_DRAG_ENTERED => if (event.getClipDescription.getLabel == "newPrec") v.setBackground(ContextCompat.getDrawable(this, R.drawable.solid_border))
            case DragEvent.ACTION_DRAG_EXITED => v.setBackground(null)
            case DragEvent.ACTION_DRAG_ENDED => v.setBackground(null)
            case DragEvent.ACTION_DROP => {
                if (event.getClipDescription.getLabel == "newPrec") addToPrecedence()
                v.setBackground(null)
            }
            case _ =>
        }
        true
    }
}
