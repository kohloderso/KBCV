package ck.kbcv.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.{Menu, MenuItem}
import ck.kbcv._
import ck.kbcv.adapters.CreateEquationsPagerAdapter
import ck.kbcv.dialogs.{AddDialogFragment, ImportDialogFragment, SaveDialogFragment}
import ck.kbcv.fragments.{CreateEquationsFragment, SymbolsFragment}
import term.Term
import term.parser.{Parser, ParserOldTRS, ParserXmlTRS}
import term.reco.{IE, IES}


class CreateEquationsActivity extends NavigationDrawerActivity with OnSymbolsChangedListener with OnEquationsChangedListener with TypedFindView with UndoRedoActivity {
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
        val utility = new ScreenUtility(this)
        if (utility.getWidth() < 400.0) {
            // Display tabLayout on small screens

            val tabLayout = findView(TR.tab_layout)
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.symbols)))
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.equations)))
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)

            val viewPager = findView(TR.viewpager)
            val fm = getSupportFragmentManager

            equationPagerAdapter = new CreateEquationsPagerAdapter(fm, context = this)
            viewPager.setAdapter(equationPagerAdapter)
            tabLayout.setupWithViewPager(viewPager)
        }

        // if the app was opened because the user clicked on a .trs or .xml file with an equational system, load that equational system
        val data = getIntent.getData
        val SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext)
        if(data != null) {
            getIntent.setData(null)
            var parser: Parser = ParserXmlTRS
            if(data.getPath.contains(".trs")) parser = ParserOldTRS
            val stream = getContentResolver.openInputStream(data)
            val es = parser.parse(stream)
            Controller.setES(es, getResources.getString(R.string.ok_new_es, new Integer(es.size)))
        } else if(SP.getBoolean("FIRST_START", true)) {    // check if it's the first run of the app
            startActivity(new Intent(getApplicationContext, classOf[PagerActivity]))
        // load default example and run intro
            val stream = getResources.openRawResource(R.raw.gt)
            val parser: Parser = ParserOldTRS
            val es = parser.parse(stream)
            Controller.setES(es, getResources.getString(R.string.ok_new_es, new Integer(es.size)))
            SP.edit().putBoolean("FIRST_START", false).commit()
        }else if (savedInstanceState != null && savedInstanceState.containsKey("equation")) {
            val equation = savedInstanceState.getSerializable("equation").asInstanceOf[(Term, Term)]
            val index = savedInstanceState.getInt("index")
            if (equationPagerAdapter != null) equationPagerAdapter.setEquation((index, equation))
            else getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment].equationEditView.setEquation((index, equation))
        }
        updateViews()
    }

    override def onSaveInstanceState(outState: Bundle): Unit = {
        super.onSaveInstanceState(outState)
        try {
            var equationsFragment: CreateEquationsFragment = null
            if (equationPagerAdapter != null) {
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                equationsFragment = getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            outState.putSerializable("equation", equationsFragment.equationEditView.getEquation.toTuple)
            outState.putInt("index", equationsFragment.equationEditView.index)
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ex.getMessage)
            }
        }
    }

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_equation_editor)
        getSupportActionBar.setTitle(getString(R.string.equation_editor))
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
            case R.id.action_clear =>{
                Controller.clearAll(getString(R.string.clear_all))
                updateViews()
                true
            }
            case _ => super.onOptionsItemSelected(item)
        }
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
            equationsFragment.equationEditView.onVariablesChanged()
            symbolsFragment.onVariablesChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, "" + ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, "" + ex.getMessage)
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
            equationsFragment.equationEditView.onFunctionsChanged()
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, "" +ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ""+ex.getMessage)
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
                Log.e(TAG, ""+ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ""+ex.getMessage)
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
                Log.e(TAG, ""+ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ""+ex.getMessage)
            }
        }
        invalidateOptionsMenu()
    }

    override def onEquationUpdated(ie: IE): Unit = {
        try {
            var equationsFragment: CreateEquationsFragment = null
            if(equationPagerAdapter != null) {
                equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[CreateEquationsFragment]
            } else {
                equationsFragment =  getSupportFragmentManager.findFragmentById(R.id.create_es_fragment).asInstanceOf[CreateEquationsFragment]
            }
            equationsFragment.onEquationUpdated(ie)
        } catch {
            case ex: ClassCastException => {
                Log.e(TAG, ""+ex.getMessage)
            }
            case ex: NullPointerException => {
                Log.e(TAG, ""+ex.getMessage)
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
