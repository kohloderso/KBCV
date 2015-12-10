package ck.kbcv

import android.content.{Intent, Context}
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.{ Button, Toast }

class MainActivity extends AppCompatActivity with TypedFindView {
    override def onCreate( savedInstanceState: Bundle ): Unit = {
        val test = new EquationsFragment
        val test2 = new CreateEquationsActivity
        //val fm = getSupportFragmentManager
        //val test3 = new EquationsPagerAdapter(fm)
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
    }

}