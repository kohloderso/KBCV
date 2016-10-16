package ck.kbcv.adapters

import android.content.Context
import android.support.v4.app.{Fragment, FragmentManager}
import ck.kbcv.fragments.{CreateEquationsFragment, SymbolsFragment}
import ck.kbcv.{R, SmartFragmentStatePagerAdapter}
import term.reco.IE

class CreateEquationsPagerAdapter(fm: FragmentManager, var ie: IE = null, context: Context) extends SmartFragmentStatePagerAdapter(fm) {
    val PAGE_COUNT = 2
    val tabTitles = Array(context.getString(R.string.symbols), context.getString(R.string.equations))

    def setEquation(ie: IE): Unit = {
        this.ie = ie
    }

    override def getCount: Int = PAGE_COUNT

    override def getItem(position: Int): Fragment = {
        position match {
            case 0 => new SymbolsFragment
            case 1 => new CreateEquationsFragment(ie)
        }
    }

    override def getPageTitle(position: Int): CharSequence = {
        tabTitles(position)
    }


}
