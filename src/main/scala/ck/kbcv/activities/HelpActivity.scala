package ck.kbcv.activities

import android.app.Fragment
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import ck.kbcv.fragments.{HelpCompletion, HelpEquationEditor}
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
        drawerToggle.setDrawerIndicatorEnabled(false)
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

  }

  def switchFragment(fragment: Fragment): Unit = {
    drawerToggle.setDrawerIndicatorEnabled(false)

  }


}
