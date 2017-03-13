package ck.kbcv.activities

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import ck.kbcv.fragments.help._
import ck.kbcv.{R, TR, TypedFindView}

class HelpActivity extends NavigationDrawerActivity with TypedFindView {

  override def onCreate( savedInstanceState: Bundle ): Unit = {
    super.onCreate( savedInstanceState )

    setContentView( R.layout.help_acitivity)
    val eqEditorButton = findView(TR.eqeditorButton)
    eqEditorButton.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        val fragment = new HelpEquationEditor
        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
      }
    })

    val completionButton = findView(TR.completionButton)
    completionButton.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        val fragment = new HelpCompletion
        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
        drawerToggle.setDrawerIndicatorEnabled(false)
      }
    })

    val settingsButton = findView(TR.settingsButton)
    settingsButton.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        val fragment = new HelpSettings
        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
        drawerToggle.setDrawerIndicatorEnabled(false)
      }
    })

    val precedenceButton = findView(TR.precedenceButton)
    precedenceButton.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        val fragment = new HelpPrecedence
        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
        drawerToggle.setDrawerIndicatorEnabled(false)
      }
    })

    val historyButton = findView(TR.historyButton)
    historyButton.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        val fragment = new HelpHistory
        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
        drawerToggle.setDrawerIndicatorEnabled(false)
      }
    })

  }

  override def onBackPressed(): Unit = {
    drawerToggle.setDrawerIndicatorEnabled(true)
    super.onBackPressed()
  }

}
