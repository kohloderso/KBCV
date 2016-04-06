package ck.kbcv.activities

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.{Snackbar, TabLayout}
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CompletionPagerAdapter
import ck.kbcv.fragments.{RulesFragment, EquationsFragment}
import term.reco
import term.reco.IE
import term.util.{E, ES, TRS}


/**
 * Created by Christina on 05.12.2015.
 */
class CompletionActivity extends AppCompatActivity with TypedFindView with CompletionActionListener {
    val TAG = "CompletionActivity"
    var completionPagerAdapter: CompletionPagerAdapter = null

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
        //inflater.inflate(R.menu.equation_menu, menu) TODO
        return true
    }


    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        // TODO
        return false
    }

    override def orientRL(e: IE): Boolean = {
        val tm = Controller.getTMIncremental(Controller.state.precedence) _
        try {
            val (erch, op) = reco.orientR(Controller.emptyI + e._1, Controller.state.erc, tm)
            if(erch == Controller.state.erc) {
                showErrorMsg("Equation couldn't be oriented")
                false
            } else {
                showSuccessMsg("Yeah!")
                Controller.state.erc = erch
                Controller.state.precedence = op.get
                val equationsfr = completionPagerAdapter.getRegisteredFragment(0).asInstanceOf[EquationsFragment]
                equationsfr.updateEquations()
                val rulesfr = completionPagerAdapter.getRegisteredFragment(1).asInstanceOf[RulesFragment]
                rulesfr.updateRules()
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

    override def orientLR(e: IE): Boolean = {
        val tm = Controller.getTMIncremental(Controller.state.precedence) _
        try {
            val (erch, op) = reco.orientL(Controller.emptyI + e._1, Controller.state.erc, tm)
            if(erch == Controller.state.erc) {
                showErrorMsg("Equation couldn't be oriented")
                false
            } else {
                showSuccessMsg("Yeah!")
                Controller.state.erc = erch
                Controller.state.precedence = op.get
                val equationsfr = completionPagerAdapter.getRegisteredFragment(0).asInstanceOf[EquationsFragment]
                equationsfr.updateEquations()
                val rulesfr = completionPagerAdapter.getRegisteredFragment(1).asInstanceOf[RulesFragment]
                rulesfr.updateRules()
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

    override def compose(trs: TRS): Unit = ???

    override def delete(e: E): Unit = ???

    override def deduce(trs: TRS): Unit = ???

    override def collapse(trs: TRS): Unit = ???

    override def simplify(es: ES): Unit = ???

    def showErrorMsg(message: String): Unit = {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("Undo", null)
            .setActionTextColor(Color.RED)
            .show();
    }

    def showSuccessMsg(message: String): Unit = {
        // TODO
    }
}
