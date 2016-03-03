package ck.kbcv.adapters

import android.support.v4.app.{Fragment, FragmentManager}
import ck.kbcv.SmartFragmentStatePagerAdapter
import ck.kbcv.fragments.{CreateEquationsFragment, SymbolsFragment}

/**
 * Created by Christina on 09.12.2015.
 */
class CreateEquationsPagerAdapter(fm: FragmentManager) extends SmartFragmentStatePagerAdapter(fm) {
    val PAGE_COUNT = 2
    val tabTitles = Array("Symbols", "Equations" )

    override def getCount: Int = PAGE_COUNT

    override def getItem(position: Int): Fragment = {
        position match {
            case 0 => new SymbolsFragment
            case 1 => new CreateEquationsFragment
        }
    }

    override def getPageTitle(position: Int): CharSequence = {
        tabTitles(position)
    }


}
