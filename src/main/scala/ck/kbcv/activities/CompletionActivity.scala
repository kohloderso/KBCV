package ck.kbcv.activities

import android.view.View
import android.graphics.{PorterDuff, Color}
import android.os.Bundle
import android.support.design.widget.{Snackbar, TabLayout}
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import android.widget.{LinearLayout, ArrayAdapter, ListView}
import ck.kbcv._
import ck.kbcv.adapters.CompletionPagerAdapter
import ck.kbcv.dialogs.{ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{RulesFragment, EquationsFragment}
import term.reco
import term.reco.{OLS, IS, I, IE}
import term.util.{E, ES, TRS, S}


/**
 * Created by Christina on 05.12.2015.
 */
class CompletionActivity extends NavigationDrawerActivity with TypedFindView with CompletionActionListener with UpdateListener {
    val TAG = "CompletionActivity"
    var completionPagerAdapter: CompletionPagerAdapter = null

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.completion_activity)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        val utility = new ScreenUtility(this)

        if (utility.getWidth() < 400.0) {   // Display tabLayout on small screens

            val tabLayout = findView(TR.tab_layout)
            tabLayout.addTab(tabLayout.newTab().setText("Equations"))
            tabLayout.addTab(tabLayout.newTab().setText("Rules"))
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

            val viewPager = findView(TR.viewpager)
            val fm = getSupportFragmentManager

            completionPagerAdapter = new CompletionPagerAdapter(fm)
            viewPager.setAdapter(completionPagerAdapter)
            tabLayout.setupWithViewPager(viewPager)
        }

    }


    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.completion_menu, menu)
        return true
    }

    override def onPrepareOptionsMenu(menu: Menu): Boolean = {
        val undoEnabled = Controller.undoable(1)
        val undoItem =  menu.findItem(R.id.undo)
        val resIcon = getResources().getDrawable(R.drawable.ic_undo_white_24dp)
        if(!undoEnabled) {
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }
        undoItem.setEnabled(undoEnabled)
        undoItem.setIcon(resIcon)

        val redoEnabled = Controller.redoable(1)
        val redoItem =  menu.findItem(R.id.redo)
        val resIcon2 = getResources().getDrawable(R.drawable.ic_redo_white_24dp)
        if(!redoEnabled) {
            resIcon2.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }
        redoItem.setEnabled(redoEnabled)
        redoItem.setIcon(resIcon2)
        true
    }


    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.completeness_check => {
                val complete = Controller.ercIsComplete()
                if(complete) showSuccessMsg("TRS is complete now")
                else showErrorMsg("Not complete yet")
                true
            }
            case R.id.undo => {
                Controller.undo()
                updateViews()
                true
            }
            case R.id.redo => {
                Controller.redo()
                updateViews()
                true
            }
            case _ => false
        }
    }

    override def orientRL(is: IS): Boolean = {
        val tm = Controller.getTMIncremental(Controller.state.precedence) _
        try {
            val (erch, op) = reco.orientR(Controller.emptyI ++ is.keys, Controller.state.erc, tm)
            if(erch == Controller.state.erc) {
                showErrorMsg("Equation couldn't be oriented")
                false
            } else {
                val message = getString(R.string.ok_orient, new Integer(is.size))
                showSuccessMsg(message)
                Controller.builder.
                    withErch(erch).
                    withPrecedence(op.get).
                    withMessage(message).
                    updateState()
                updateViews()
                true
            }
        } catch {
            case ex: IllegalArgumentException => {
                Log.e(TAG, ex.getMessage)
                showErrorMsg(ex.getMessage)
                false
            }
        }
    }

    override def orientLR(is: IS): Boolean = {
        val tm = Controller.getTMIncremental(Controller.state.precedence) _
        try {
            val (erch, op) = reco.orientL(Controller.emptyI ++ is.keys, Controller.state.erc, tm)
            if(erch == Controller.state.erc) {
                showErrorMsg("Equation couldn't be oriented")
                false
            } else {
                val message = getString(R.string.ok_orient, new Integer(is.size))
                showSuccessMsg(message)
                Controller.builder.
                    withErch(erch).
                    withPrecedence(op.get).
                    withMessage(message).
                    updateState()
                updateViews()
                true
            }
        } catch {
            case ex: IllegalArgumentException => {
                Log.e(TAG, ex.getMessage)
                showErrorMsg(ex.getMessage)
                false
            }
        }
    }


    override def delete(is: IS): Unit = {
        val erch = reco.delete(Controller.emptyI ++ is.keys, Controller.state.erc)
        val numberNew = Controller.state.erc._1.size - erch._1.size
        if(numberNew > 0) {
            val message = getString(R.string.ok_delete, new Integer(numberNew))
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withMessage(message).
                updateState()
            updateEquationFragment()
            invalidateOptionsMenu()
        } else {
            showErrorMsg(getString(R.string.error_delete))
        }
    }

    override def simplify(is: IS): Unit = {
        val erch = reco.simplifyToNF(Controller.emptyS, Controller.emptyTI, Controller.state.depth)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            val message = getString(R.string.ok_simplify)
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withMessage(message).
                updateState()
            updateEquationFragment()
            invalidateOptionsMenu()
        } else {
            showErrorMsg(getString(R.string.error_simplify))
        }
    }

    override def compose(is: IS): Unit = {
        val erch = reco.composeToNF(Controller.emptyS, Controller.emptyTI, Controller.state.depth)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            val message = getString(R.string.ok_compose)
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withMessage(message).
                updateState()
            updateRulesFragment()
            invalidateOptionsMenu()
        } else {
            showErrorMsg(getString(R.string.error_compose))
        }
    }

    override def collapse(is: IS): Unit = {
        val erch = reco.collapse(Controller.emptyS, Controller.emptyTI)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            val message = getString(R.string.ok_collapse)
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withMessage(message).
                updateState()
            updateViews()
        } else {
            showErrorMsg(getString(R.string.error_collapse))
        }
    }

    override def deduce(is: IS): Unit = {
        // TODO deduce caching
        val erch = reco.deduce(new OLS, Controller.emptyTI)(Controller.emptyI ++ is.keys, Controller.state.erc)
        val numberNew = erch._1.size - Controller.state.erc._1.size
        if(numberNew > 0) {
            val message = getString(R.string.ok_deduced, new Integer(numberNew))
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withMessage(message).
                updateState()
            updateViews()
        } else {
            showErrorMsg(getString(R.string.error_deduce))
        }
    }

    def showErrorMsg(message: String): Unit = {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show();
    }

    def showSuccessMsg(message: String): Unit = {
        val onClickListener = new View.OnClickListener {
            override def onClick(v: View) {
                Controller.undo()
                updateViews()
            }
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("undo", onClickListener)
            .show();
    }

    override def updateViews(): Unit = {
        invalidateOptionsMenu()
        updateEquationFragment()
        updateRulesFragment()
    }

    def updateEquationFragment(): Unit = {
        var equationsfr: EquationsFragment = null
        if(completionPagerAdapter != null) {
            equationsfr = completionPagerAdapter.getRegisteredFragment(0).asInstanceOf[EquationsFragment]
        } else {
            equationsfr = getSupportFragmentManager.findFragmentById(R.id.equations_fragment).asInstanceOf[EquationsFragment]
        }

        equationsfr.updateEquations()
    }

    def updateRulesFragment(): Unit = {
        var rulesfr: RulesFragment = null
        if(completionPagerAdapter != null) {
            rulesfr = completionPagerAdapter.getRegisteredFragment(1).asInstanceOf[RulesFragment]
        } else {
            rulesfr = getSupportFragmentManager.findFragmentById(R.id.rules_fragment).asInstanceOf[RulesFragment]
        }
        rulesfr.updateRules()
    }
}
