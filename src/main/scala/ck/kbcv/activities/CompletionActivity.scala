package ck.kbcv.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CompletionPagerAdapter
import ck.kbcv.dialogs.{AddDialogFragment, ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{EquationsFragment, CreateEquationsFragment, SymbolsFragment}
import term.reco.IES
import term.util.ES


/**
 * Created by Christina on 05.12.2015.
 */
class CompletionActivity extends AppCompatActivity with TypedFindView {
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

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        /*if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val firstFragment = new NewEquationFragment()

            // Add the fragment to the 'fragment_container' FrameLayout
            //getSupportFragmentManager().beginTransaction()
              //.add(R.id.fragment_container, firstFragment).commit()
        }*/
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

}
