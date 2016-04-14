package ck.kbcv.activities

import android.view.View
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.{Snackbar, TabLayout}
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import android.widget.{ArrayAdapter, ListView}
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
class CompletionActivity extends NavigationDrawerActivity with TypedFindView with CompletionActionListener {
    val TAG = "CompletionActivity"
    var completionPagerAdapter: CompletionPagerAdapter = null
    var mDrawerList: ListView = null

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.completion_activity)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        val tabLayout = findView(TR.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("Equations"))
        tabLayout.addTab(tabLayout.newTab().setText("Rules"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

        val viewPager = findView(TR.viewpager)
        val fm =  getSupportFragmentManager

        completionPagerAdapter = new CompletionPagerAdapter(fm)
        viewPager.setAdapter(completionPagerAdapter)
        tabLayout.setupWithViewPager(viewPager)

    }


    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.completion_menu, menu)
        return true
    }


    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.completeness_check => {
                val complete = Controller.ercIsComplete()
                if(complete) showSuccessMsg("TRS is complete now")
                else showErrorMsg("Not complete yet")
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
                showSuccessMsg(is.size + " equations oriented")
                Controller.state.erc = erch
                Controller.state.precedence = op.get
                updateEquationFragment()
                updateRulesFragment()
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
                showSuccessMsg(is.size + " equations oriented")
                Controller.state.erc = erch
                Controller.state.precedence = op.get
                updateEquationFragment()
                updateRulesFragment()
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
        if(erch != Controller.state.erc) {
            showSuccessMsg(getString(R.string.ok_delete))
            Controller.state.erc = erch
            updateEquationFragment()
        } else {
            showErrorMsg(getString(R.string.error_delete))
        }
    }

    override def simplify(is: IS): Unit = {
        val erch = reco.simplifyToNF(Controller.emptyS, Controller.emptyTI, Controller.state.depth)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            showSuccessMsg(getString(R.string.ok_simplify))
            Controller.state.erc = erch
            updateEquationFragment()
        } else {
            showErrorMsg(getString(R.string.error_simplify))
        }
    }

    override def compose(is: IS): Unit = {
        val erch = reco.composeToNF(Controller.emptyS, Controller.emptyTI, Controller.state.depth)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            showSuccessMsg(getString(R.string.ok_compose))
            Controller.state.erc = erch
            updateRulesFragment()
        } else {
            showErrorMsg(getString(R.string.error_compose))
        }
    }

    override def collapse(is: IS): Unit = {
        val erch = reco.collapse(Controller.emptyS, Controller.emptyTI)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            showSuccessMsg(getString(R.string.ok_collapse))
            Controller.state.erc = erch
            updateEquationFragment()
            updateRulesFragment()
        } else {
            showErrorMsg(getString(R.string.error_collapse))
        }
    }

    override def deduce(is: IS): Unit = {
        // TODO deduce caching
        val erch = reco.deduce(new OLS, Controller.emptyTI)(Controller.emptyI ++ is.keys, Controller.state.erc)
        if(erch != Controller.state.erc) {
            showSuccessMsg(getString(R.string.ok_deduced))
            Controller.state.erc = erch
            updateEquationFragment()
            updateRulesFragment()
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
                // TODO Undo functionality
            }
        }
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("undo", onClickListener)
            .show();
    }

    def updateEquationFragment(): Unit = {
        val equationsfr = completionPagerAdapter.getRegisteredFragment(0).asInstanceOf[EquationsFragment]
        equationsfr.updateEquations()
    }

    def updateRulesFragment(): Unit = {
        val rulesfr = completionPagerAdapter.getRegisteredFragment(1).asInstanceOf[RulesFragment]
        rulesfr.updateRules()
    }
}
