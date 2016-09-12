package ck.kbcv.activities

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.FrameLayout
import ck.kbcv.R
import ck.kbcv.dialogs.HistoryDialogFragment


class NavigationDrawerActivity extends AppCompatActivity with NavigationView.OnNavigationItemSelectedListener {
    var drawerLayout: DrawerLayout = null
    var navigationView: NavigationView = null

    override def setContentView(layoutResID: Int) = {

        drawerLayout = getLayoutInflater.inflate(R.layout.navigation_drawer, null).asInstanceOf[DrawerLayout]
        val mainLayout = drawerLayout.findViewById(R.id.main_content).asInstanceOf[FrameLayout]
        navigationView = drawerLayout.findViewById(R.id.nav_view).asInstanceOf[NavigationView]
        navigationView.setNavigationItemSelectedListener(this)

        getLayoutInflater.inflate(layoutResID, mainLayout, true)
        super.setContentView(drawerLayout)
    }


    override def onNavigationItemSelected(menuItem: MenuItem): Boolean = {
        menuItem.getItemId match {
            case R.id.action_completion => {
                startDrawerActivity(classOf[CompletionActivity])
                true
            }
            case R.id.action_equation_editor => {
                startDrawerActivity(classOf[CreateEquationsActivity])
                true
            }
            case R.id.action_history => {
                new HistoryDialogFragment().show(getSupportFragmentManager, "HistoryDialog")
                true
            }
            case R.id.action_precedence => {
                startDrawerActivity(classOf[PrecedenceActivity])
                true
            }
            case R.id.action_settings => {
                startDrawerActivity(classOf[SettingsActivity])
                true
            }
            case _ => false
        }
    }


    def startDrawerActivity(activity: Class[_]): Unit = {
        drawerLayout.closeDrawer(GravityCompat.START)
        startActivity(new Intent(getApplicationContext, activity))

    }

}
