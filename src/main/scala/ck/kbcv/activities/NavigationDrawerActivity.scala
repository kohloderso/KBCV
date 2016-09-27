package ck.kbcv.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.{ActionBarDrawerToggle, AppCompatActivity}
import android.view.MenuItem
import android.widget.FrameLayout
import ck.kbcv.dialogs.HistoryDialogFragment
import ck.kbcv.{R, TR, TypedFindView}


class NavigationDrawerActivity extends AppCompatActivity with NavigationView.OnNavigationItemSelectedListener with TypedFindView {
    var drawerLayout: DrawerLayout = null
    var navigationView: NavigationView = null
    var drawerToggle: ActionBarDrawerToggle = null

    override def setContentView(layoutResID: Int) = {

        drawerLayout = getLayoutInflater.inflate(R.layout.navigation_drawer, null).asInstanceOf[DrawerLayout]
        val mainLayout = drawerLayout.findViewById(R.id.main_content).asInstanceOf[FrameLayout]
        navigationView = drawerLayout.findViewById(R.id.nav_view).asInstanceOf[NavigationView]
        navigationView.setNavigationItemSelectedListener(this)

        getLayoutInflater.inflate(layoutResID, mainLayout, true)
        super.setContentView(drawerLayout)

        val myToolbar = findView(TR.my_toolbar)
        setSupportActionBar(myToolbar)

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
    }

    override def onPostCreate(savedInstanceState: Bundle): Unit = {
        drawerToggle.syncState()
        super.onPostCreate(savedInstanceState)
    }

    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        if (drawerToggle.onOptionsItemSelected(item)) return true
        super.onOptionsItemSelected(item)
    }

    override def onBackPressed(): Unit = {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
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
        drawerLayout.closeDrawers()
        drawerLayout.postDelayed(new Runnable() {
            override def run() {
                startActivity(new Intent(getApplicationContext, activity))
            }
        }, 300);

    }

}
