package ck.kbcv.activities

import android.app.Activity
import android.content.Intent
import android.graphics.{PorterDuff, Color}
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CreateEquationsPagerAdapter
import ck.kbcv.dialogs.{AddDialogFragment, ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{CreateEquationsFragment, SymbolsFragment}
import term.reco.IES
import term.util.ES


/**
 * Created by Christina on 05.12.2015.
 */
class CreateEquationsActivity extends NavigationDrawerActivity with UpdateListener with OnSymbolsChangedListener with OnEquationsChangedListener with TypedFindView {
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

        val utility = new ScreenUtility(this)

        if (utility.getWidth() < 400.0) {
            // Display tabLayout on small screens

            val tabLayout = findView(TR.tab_layout)
            tabLayout.addTab(tabLayout.newTab().setText("Symbols"))
            tabLayout.addTab(tabLayout.newTab().setText("Equations"))
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

            val viewPager = findView(TR.viewpager)
            val fm = getSupportFragmentManager

            equationPagerAdapter = new CreateEquationsPagerAdapter(fm)
            viewPager.setAdapter(equationPagerAdapter)
            tabLayout.setupWithViewPager(viewPager)
        }

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


    def updateViews(): Unit = {
        invalidateOptionsMenu()
        onVariablesChanged()
        onFunctionsChanged()
        onNewEquations()    // TODO not very good performance
    }

    override def onVariablesChanged(): Unit = {
        try {
            var equationsFragment: CreateEquationsFragment = null
            var symbolsFragment: SymbolsFragment = null
            if(equationPagerAdapter != null) {
                symbolsFragment = equationPagerAdapter.getRegisteredFragment(0).asInstanceOf[SymbolsFragment]
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                symbolsFragment = getSupportFragmentManager.findFragmentById(R.id.symbols_fragment).asInstanceOf[SymbolsFragment]
                equationsFragment =  getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            equationsFragment.onVariablesChanged()
            symbolsFragment.onVariablesChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
        invalidateOptionsMenu()
    }

    override def onFunctionsChanged(): Unit = {
        try {
            var equationsFragment: CreateEquationsFragment = null
            var symbolsFragment: SymbolsFragment = null
            if(equationPagerAdapter != null) {
                symbolsFragment = equationPagerAdapter.getRegisteredFragment(0).asInstanceOf[SymbolsFragment]
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                symbolsFragment = getSupportFragmentManager.findFragmentById(R.id.symbols_fragment).asInstanceOf[SymbolsFragment]
                equationsFragment =  getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            symbolsFragment.onFunctionsChanged()
            equationsFragment.onFunctionsChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
        invalidateOptionsMenu()
    }

    override def onNewEquations(): Unit = {
        try {
            var equationsFragment: CreateEquationsFragment = null
            if(equationPagerAdapter != null) {
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                equationsFragment =  getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            equationsFragment.onNewEquations()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
        invalidateOptionsMenu()
    }

    override def onEquationsAdded(): Unit = {
        try {
            var equationsFragment: CreateEquationsFragment = null
            if(equationPagerAdapter != null) {
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                equationsFragment =  getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            equationsFragment.onEquationsAdded()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
        invalidateOptionsMenu()
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
