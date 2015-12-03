package ck.kbcv

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.{ Button, Toast }

class MainActivity extends AppCompatActivity with TypedFindView {
    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        val context = getApplicationContext();

        setContentView( R.layout.main )

        findView( TR.new_button ).setOnClickListener( new View.OnClickListener() {
            def onClick( v: View ) {
                setContentView( R.layout.new_equation )
            }
        } )
    }

}