package ck.kbcv.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CreateEquationsPagerAdapter
import ck.kbcv.dialogs.{AddDialogFragment, ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{SymbolsFragment, CreateEquationsFragment}
import term.reco.IES
import term.util.ES


/**
 * Created by Christina on 05.12.2015.
 */
class CreateEquationsActivity extends AppCompatActivity with OnSymbolsChangedListener with OnEquationsChangedListener with TypedFindView {
    val TAG = "CreateEquationsActivity"
    var equationPagerAdapter: CreateEquationsPagerAdapter = null
    var ies: IES = null
    var mfunctions: Map[String, Int] = Map()
    var mvariables: Array[String] = Array()
    val FILE_REQUEST = 1
    private var mReturningWithResult = false
    private var mResultUri: Uri = null


    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.create_es_activity)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        val tabLayout = findView(TR.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("Symbols"))
        tabLayout.addTab(tabLayout.newTab().setText("Equations"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

        val viewPager = findView(TR.viewpager)
        val test =  getSupportFragmentManager

        equationPagerAdapter = new CreateEquationsPagerAdapter(test)
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
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
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
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
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

    override def onNewEquations(newES: ES): Unit = {
        try {
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            equationsFragment.onNewEquations(newES)
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
    }

    override def onEquationsAdded(addedES: ES): Unit = {
        try {
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            equationsFragment.onEquationsAdded(addedES)
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
    }

    override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
        // Check which request we're responding to
        if (requestCode == FILE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                mReturningWithResult = true
                mResultUri = data.getData
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override def onPostResume(): Unit = {
        super.onPostResume()
        if(mReturningWithResult) {
            mReturningWithResult = false
            val bundle = new Bundle()
            bundle.putCharSequence("uri", mResultUri.toString)
            val addDialog = new AddDialogFragment()
            addDialog.setArguments(bundle)
            addDialog.show(getSupportFragmentManager, "AddDialog")
        }
    }
}
