package ck.kbcv

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ck.kbcv.activities.{NavigationDrawerActivity, CompletionActivity, CreateEquationsActivity}

class MainActivity extends NavigationDrawerActivity with TypedFindView with UpdateListener{

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

    override def updateViews(): Unit = {
        // nothing to update
    }

}