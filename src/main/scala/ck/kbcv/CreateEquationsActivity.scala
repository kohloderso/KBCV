package ck.kbcv

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.FragmentManager
import android.util.Log


/**
 * Created by Christina on 05.12.2015.
 */
class CreateEquationsActivity extends AppCompatActivity with OnVariablesChangedListener with TypedFindView {
    val TAG = "CreateEquationsActivity"
    var equationPagerAdapter: EquationsPagerAdapter = null

    @Override
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

    override def onVariablesChanged(variables: Array[String]): Unit = {
        try {
            val equationsFragment = equationPagerAdapter.getRegisteredFragment(1).asInstanceOf[EquationsFragment]
            equationsFragment.onVariablesChanged(variables)
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
