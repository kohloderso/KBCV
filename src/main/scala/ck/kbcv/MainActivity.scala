package ck.kbcv

import java.io.{File, OutputStreamWriter}

import android.app.Activity
import android.content.{Intent, Context}
import android.net.Uri
import android.os.{Environment, Bundle}
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.{ Button, Toast }
import ck.kbcv.state.{MutableState, State}
import term.{Term, Var, Fun}
import term.Term._
import term.parser.ParserXmlTRS
import term.reco.IES
import term.util._

class MainActivity extends AppCompatActivity with TypedFindView {
    val state = new MutableState(new State(Nil, Set() , Set()))
    val FILE_REQUEST = 1

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
                // TODO
            }
        } )
    }

    def chooseFile(): Unit = {

        val intent = new Intent(Intent.ACTION_GET_CONTENT)
        //intent.setType("*/*");      //all files
        intent.setType("text/xml");   //XML file only
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
          startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_REQUEST)
        } catch {
            case ex: android.content.ActivityNotFoundException => {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
        // Check which request we're responding to
        if (requestCode == FILE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }
}