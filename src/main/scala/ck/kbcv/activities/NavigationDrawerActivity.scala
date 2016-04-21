package ck.kbcv.activities

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.FrameLayout
import ck.kbcv.R
import ck.kbcv.dialogs.{HistoryDialogFragment, ImportDialogFragment, SaveDialogFragment}

/**
 * Created by Christina on 13.04.2016.
 */
class NavigationDrawerActivity extends AppCompatActivity with NavigationView.OnNavigationItemSelectedListener {
    var mainLayout: FrameLayout = null

    override def setContentView(layoutResID: Int) = {

        val fullLayout = getLayoutInflater.inflate(R.layout.navigation_drawer, null)
        mainLayout = fullLayout.findViewById(R.id.main_content).asInstanceOf[FrameLayout]
        val navigationView = fullLayout.findViewById(R.id.nav_view).asInstanceOf[NavigationView]
        navigationView.setNavigationItemSelectedListener(this)

        getLayoutInflater.inflate(layoutResID, mainLayout, true)
        super.setContentView(fullLayout)
    }


    override def onNavigationItemSelected(menuItem: MenuItem): Boolean = {
        menuItem.getItemId match {
            case R.id.action_completion => {
                val intent = new Intent(getApplicationContext, classOf[CompletionActivity])
                startActivity(intent)
                true
            }
            case R.id.action_equation_editor => {
                val intent = new Intent(getApplicationContext, classOf[CreateEquationsActivity])
                startActivity(intent)
                true
            }
            case R.id.action_history => {
                new HistoryDialogFragment().show(getSupportFragmentManager, "HistoryDialog")
                true
            }
            case _ => false
        }
    }
}
