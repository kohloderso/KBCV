package ck.kbcv.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CompletionPagerAdapter
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

    override def orientRL(e: E): Unit = ???

    override def orientLR(e: E): Unit = ???

    override def compose(trs: TRS): Unit = ???

    override def delete(e: E): Unit = ???

    override def deduce(trs: TRS): Unit = ???

    override def collapse(trs: TRS): Unit = ???

    override def simplify(es: ES): Unit = ???
}
