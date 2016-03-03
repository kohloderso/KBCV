package ck.kbcv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import ck.kbcv.activities.{CompletionActivity, CreateEquationsActivity}
import ck.kbcv.dialogs.AddDialogFragment

class MainActivity extends AppCompatActivity with TypedFindView {
    val state = new MutableState(new State(Nil, Set() , Set()))

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        val context = getApplicationContext()

        setContentView( R.layout.main )

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        findView( TR.new_button ).setOnClickListener( new View.OnClickListener() {
            def onClick( v: View ) {
                val intent = new Intent(context, classOf[CreateEquationsActivity])
                startActivity(intent)
            }
        } )

        findView( TR.load_button ).setOnClickListener( new View.OnClickListener() {
            def onClick( v: View ): Unit = {
                val intent = new Intent(context, classOf[CompletionActivity])
                startActivity(intent)
            }
        } )
    }

    override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
       super.onActivityResult(requestCode, resultCode, data)
    }
}