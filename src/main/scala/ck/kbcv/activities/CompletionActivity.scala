package ck.kbcv.activities

import android.app.{Activity, ProgressDialog}
import android.content.SharedPreferences
import android.graphics.{Color, PorterDuff}
import android.os.{AsyncTask, Bundle}
import android.support.design.widget.{Snackbar, TabLayout}
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.{Menu, MenuItem, View}
import ck.kbcv._
import ck.kbcv.adapters.CompletionPagerAdapter
import ck.kbcv.dialogs.SaveDialogFragment
import ck.kbcv.fragments.{EquationsFragment, RulesFragment}
import term.reco
import term.reco._


class CompletionActivity extends NavigationDrawerActivity with TypedFindView with CompletionActionListener with UpdateListener {
    val TAG = "CompletionActivity"
    val CREATE_REQUEST_CODE = 40
    var completionPagerAdapter: CompletionPagerAdapter = null
    var SP: SharedPreferences = null

    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.completion_activity)

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext)

        val utility = new ScreenUtility(this)

        if (utility.getWidth() < 400.0) {
            // Display tabLayout on small screens

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

    override def onResume(): Unit = {
        super.onResume()
        navigationView.setCheckedItem(R.id.action_completion)
    }


    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        val inflater = getMenuInflater
        inflater.inflate(R.menu.completion_menu, menu)
        return true
    }

    override def onPrepareOptionsMenu(menu: Menu): Boolean = {
        val undoEnabled = Controller.undoable(1)
        val undoItem = menu.findItem(R.id.undo)
        val resIcon = getResources().getDrawable(R.drawable.ic_undo_white_24dp)
        if (!undoEnabled) {
            resIcon.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        }
        undoItem.setEnabled(undoEnabled)
        undoItem.setIcon(resIcon)

        val redoEnabled = Controller.redoable(1)
        val redoItem = menu.findItem(R.id.redo)
        val resIcon2 = getResources().getDrawable(R.drawable.ic_redo_white_24dp)
        if (!redoEnabled) {
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
                if (complete) showSuccessMsg("TRS is complete now")
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
            case R.id.automatic_completion => {
                val runner = new AutoCompletionRunner(this)
                runner.execute()
                true
            }

            case R.id.export_rules => {
                new SaveDialogFragment(true).show(getSupportFragmentManager, "SaveDialog")
                true
            }
            case _ => super.onOptionsItemSelected(item)
        }
    }


    override def orientRL(is: IS): Boolean = {
        val tm = Controller.getTMIncremental(Controller.state.precedence) _
        try {
            val (erch, op) = reco.orientR(Controller.emptyI ++ is.keys, Controller.state.erc, tm)
            if (erch == Controller.state.erc) {
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
                if (SP.getBoolean("pref_completeness", false)) {
                    val complete = Controller.ercIsComplete()
                    if (complete) showSuccessMsg("TRS is complete now")
                }

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
            if (erch == Controller.state.erc) {
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
                if (SP.getBoolean("pref_completeness", false)) {
                    val complete = Controller.ercIsComplete()
                    if (complete) showSuccessMsg("TRS is complete now")
                }
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
        if (numberNew > 0) {
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
        if (erch != Controller.state.erc) {
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
        if (erch != Controller.state.erc) {
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
        if (erch != Controller.state.erc) {
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
        val ols: OLS = if (SP.getBoolean("pref_caching", false)) Controller.state.ols else Controller.state.ols.empty

        val erch = reco.deduce(ols, Controller.emptyTI)(Controller.emptyI ++ is.keys, Controller.state.erc)
        val numberNew = erch._1.size - Controller.state.erc._1.size
        if (numberNew > 0) {
            // compute which overlaps where considered this round
            // first get indices of new equations
            val nis = erch._1.keySet.filterNot(Controller.state.erc._1.contains(_))
            // second make list of newly considered overlaps
            // IMPORTANT: nols has to be a List, otherwise duplicates are thrown
            // out, and we don't want that to happen here!
            val nols = erch._4.filterKeys(nis.contains(_)).toList.map(t => (t._2._2._1, t._2._4._1))
            // add new overlaps to already previously considered overlaps and save
            val ols = Controller.state.ols ++ nols
            val message = getString(R.string.ok_deduced, new Integer(numberNew))
            showSuccessMsg(message)
            Controller.builder.
                withErch(erch).
                withOLS(ols).
                withMessage(message).
                updateState()
            updateViews()
            if (SP.getBoolean("pref_completeness", false)) {
                val complete = Controller.ercIsComplete()
                if (complete) showSuccessMsg("TRS is complete now")
            }
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
        if (completionPagerAdapter != null) {
            equationsfr = completionPagerAdapter.getRegisteredFragment(0).asInstanceOf[EquationsFragment]
        } else {
            equationsfr = getSupportFragmentManager.findFragmentById(R.id.equations_fragment).asInstanceOf[EquationsFragment]
        }

        equationsfr.updateEquations()
    }

    def updateRulesFragment(): Unit = {
        var rulesfr: RulesFragment = null
        if (completionPagerAdapter != null) {
            rulesfr = completionPagerAdapter.getRegisteredFragment(1).asInstanceOf[RulesFragment]
        } else {
            rulesfr = getSupportFragmentManager.findFragmentById(R.id.rules_fragment).asInstanceOf[RulesFragment]
        }
        rulesfr.updateRules()
    }

    class AutoCompletionRunner(activity: Activity) extends AsyncTask[AnyRef, AnyRef, Boolean] {
        var running = true
        var nosol = false
        var rounds = 0
        val limit = SP.getInt("number_rounds", 0)
        val depth = Controller.state.depth
        var step = Controller.state.erc
        var simps = Controller.emptyS
        var comps = Controller.emptyS
        var colls = Controller.emptyS
        var eis = step._1.keySet
        var ris = step._2.keySet
        var eat: I = Controller.emptyI
        var prec: OP = Some(Controller.state.precedence)
        var ti = Controller.emptyTI
        var ols: OLS = if (SP.getBoolean("pref_caching", false)) Controller.state.ols else Controller.state.ols.empty
        var pd: ProgressDialog = null

        def limitReached = limit != 0 && rounds >= limit

        override def doInBackground(params: AnyRef*): Boolean = {
            while (running) {
                // SIMPLIFY & DELETE
                step = reco.simplifyToNF(simps, ti, depth)(eis, step)
                simps =
                    if (depth > 0) simps ++ eis.map(k => (k, step._2.keySet)).toMap
                    else simps
                eis = step._1.keySet
                step = reco.delete(eis, step)
                eis = step._1.keySet

                if (isComplete(ols, ti)(step) || limitReached) {
                    //TODO maxNumSteps
                    running = false
                } else {
                    rounds += 1
                    publishProgress()
                }

                // ORIENT
                if (running) {
                    var i = 0
                    // smallest first
                    val l = step._1.filterKeys(!eat.contains(_))
                    // calculate a list of the smallest equations
                    if (l.isEmpty) {
                        running = false
                        // there can be no solution
                        nosol = true
                    } else {
                        // get index of "smallest" equation wrt lhs+rhs size
                        l.minBy(_._2.size)._1
                        i = l.minBy(_._2.size)._1
                        // remember rules already tried
                        eat = eat + i
                    }

                    var tmp: (ERCH, OP) = (step, prec)
                    val tm = Controller.getTMIncremental(Controller.state.precedence) _
                    try {
                        tmp = orientL(Controller.emptyI + i, step, tm)
                    } catch {
                        case _: Throwable => tmp = (step, prec)
                    }
                    try {
                        if (tmp._1 == step) tmp = orientR(Controller.emptyI + i, step, tm)
                    } catch {
                        case _: Throwable => tmp = (step, prec)
                    }
                    step = tmp._1
                    prec = tmp._2
                    eis = step._1.keySet
                    ris = step._2.keySet

                    // COMPOSE
                    step = reco.composeToNF(comps, ti, depth)(ris, step)
                    comps =
                        if (depth > 0) comps ++ ris.map(k => (k, step._2.keySet)).toMap
                        else comps
                    eis = step._1.keySet
                    ris = step._2.keySet

                    // COLLAPSE
                    step = reco.collapse(colls, ti)(ris, step)
                    colls =
                        if (depth > 0) colls ++ ris.map(k => (k, step._2.keySet)).toMap
                        else colls
                    eis = step._1.keySet
                    ris = step._2.keySet

                    // DEDUCE
                    val old = step
                    step = reco.deduce(ols, ti)(ris, step)
                    eis = step._1.keySet
                    ris = step._2.keySet

                    // compute which overlaps where considered this round
                    // first get indices of new equations
                    val nis = step._1.keySet.filterNot(old._1.contains(_))
                    // second make list of newly considered overlaps
                    // IMPORTANT: nols has to be a List, otherwise duplicates are thrown
                    // out, and we don't want that to happen here!
                    val nols = step._4.filterKeys(nis.contains(_)).toList.map(t => (t._2._2._1, t._2._4._1))
                    // add new overlaps to already previously considered overlaps and save
                    ols = ols ++ nols
                }
            }
            if (isComplete(ols, ti)(step)) return true
            false
        }

        override def onPreExecute(): Unit = {
            pd = new ProgressDialog(activity)
            pd.setMax(limit)
            pd.setProgress(0)
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            pd.setTitle("Automatic completion")
            pd.setMessage("Computing...")
            pd.setIndeterminate(false)
            pd.show()
        }

        override def onProgressUpdate(any: AnyRef*): Unit = {
            pd.setProgress(rounds)
        }

        override def onPostExecute(result: Boolean) {
            pd.dismiss()
            var message = ""
            if (result) {
                message = getString(R.string.ok_auto_complete)
                showSuccessMsg(message)
            } else {
                message = getString(R.string.error_auto_complete)
                showErrorMsg(message)
            }
            Controller.builder.
                withErch(step).
                withOLS(ols).
                withPrecedence(prec.get).
                withMessage(message).
                updateState()
            updateViews()
        }
    }

}
