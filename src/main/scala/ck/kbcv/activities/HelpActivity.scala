package ck.kbcv.activities

import android.app.Fragment
import android.os.Bundle
import ck.kbcv.{R, TypedFindView}

class HelpActivity extends NavigationDrawerActivity with TypedFindView {

  override def onCreate( savedInstanceState: Bundle ): Unit = {
    super.onCreate( savedInstanceState )

    setContentView( R.layout.help_acitivity)
   // val equationEditorCard = findView(TR.equation_editor_card)
//    equationEditorCard.setOnClickListener(new OnClickListener {
//      override def onClick(view: View): Unit = {
//        val fragment = new HelpEquationEditor
//        getSupportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
//        drawerToggle.setDrawerIndicatorEnabled(false)
//
//      }
//    })

  }

  def switchFragment(fragment: Fragment): Unit = {
    drawerToggle.setDrawerIndicatorEnabled(false)

  }


}
