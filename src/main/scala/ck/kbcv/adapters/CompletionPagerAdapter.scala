package ck.kbcv.adapters

import android.content.Context
import android.support.v4.app.{Fragment, FragmentManager}
import ck.kbcv.fragments.{EquationsFragment, RulesFragment}
import ck.kbcv.{R, SmartFragmentStatePagerAdapter}

class CompletionPagerAdapter(fm: FragmentManager, context: Context) extends SmartFragmentStatePagerAdapter(fm) {
    val PAGE_COUNT = 2
    val tabTitles = Array(context.getString(R.string.equations), context.getString(R.string.rules))

    override def getCount: Int = PAGE_COUNT

    override def getItem(position: Int): Fragment = {
        position match {
            case 0 => new EquationsFragment
            case 1 => new RulesFragment
        }
    }

    override def getPageTitle(position: Int): CharSequence = {
        tabTitles(position)
    }


}
