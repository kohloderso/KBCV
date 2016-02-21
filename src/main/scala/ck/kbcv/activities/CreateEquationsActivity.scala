package ck.kbcv.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.dialogs.{ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{SymbolsFragment, EquationsFragment}
import ck.kbcv.model.OnSymbolsChangedListener
import term.reco.IES


/**
 * Created by Christina on 05.12.2015.
 */
class CreateEquationsActivity extends AppCompatActivity with OnSymbolsChangedListener with TypedFindView {
    val TAG = "CreateEquationsActivity"
    var equationPagerAdapter: EquationsPagerAdapter = null
    var ies: IES = null
    var mfunctions: Map[String, Int] = Map()
    var mvariables: Array[String] = Array()


    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.create_equations)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        val tabLayout = findView(TR.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("Symbols"))
        tabLayout.addTab(tabLayout.newTab().setText("Equations"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

        val viewPager = findView(TR.viewpager)
        val test =  getSupportFragmentManager

        equationPagerAdapter = new EquationsPagerAdapter(test)
        viewPager.setAdapter(equationPagerAdapter)
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
        inflater.inflate(R.menu.equation_menu, menu)
        return true
    }


    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.action_save => {
                new SaveDialogFragment().show(getSupportFragmentManager, "SaveDialog")
                true
            }
            case R.id.action_load => {
                new ImportDialogFragment().show(getSupportFragmentManager, "ImportDialog")
                true
            }
            case _ => false
        }
    }


    override def onVariablesChanged(): Unit = {
        try {
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[EquationsFragment]
            equationsFragment.onVariablesChanged()

            val symbolsFragment = equationPagerAdapter.getRegisteredFragment(0).asInstanceOf[SymbolsFragment]
            symbolsFragment.onVariablesChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
    }

    override def onFunctionsChanged(): Unit = {
        try {
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[EquationsFragment]
            equationsFragment.onFunctionsChanged()

            val symbolsFragment = equationPagerAdapter.getRegisteredFragment(0).asInstanceOf[SymbolsFragment]
            symbolsFragment.onFunctionsChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
    }

}
