package ck.kbcv

import android.content.Intent
import android.os.Bundle
import android.view.View
import ck.kbcv.activities.{CompletionActivity, CreateEquationsActivity, NavigationDrawerActivity}

class MainActivity extends NavigationDrawerActivity with TypedFindView {

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        val context = getApplicationContext()

        setContentView( R.layout.main)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        findView( TR.create_button ).setOnClickListener( new View.OnClickListener() {
            def onClick( v: View ) {
                val intent = new Intent(context, classOf[CreateEquationsActivity])
                startActivity(intent)
            }
        } )

        findView( TR.completion_button ).setOnClickListener( new View.OnClickListener() {
            def onClick( v: View ): Unit = {
                val intent = new Intent(context, classOf[CompletionActivity])
                startActivity(intent)
            }
        } )
    }


}