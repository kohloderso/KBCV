package ck.kbcv

import android.content.Context
import android.support.v4.app.{Fragment, FragmentManager, FragmentPagerAdapter}

/**
 * Created by Christina on 09.12.2015.
 */
class EquationsPagerAdapter(fm: FragmentManager) extends SmartFragmentStatePagerAdapter(fm) {
    val PAGE_COUNT = 2
    val tabTitles = Array("Symbols", "Equations" )

    override def getCount: Int = PAGE_COUNT

    override def getItem(position: Int): Fragment = {
        position match {
            case 0 => new SymbolsFragment
            case 1 => new EquationsFragment
        }
    }

    override def getPageTitle(position: Int): CharSequence = {
        tabTitles(position)
    }


}
