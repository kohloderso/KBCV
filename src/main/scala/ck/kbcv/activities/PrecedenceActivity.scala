package ck.kbcv.activities

import android.os.Bundle
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import ck.kbcv._
import ck.kbcv.adapters.PrecedenceAdapter


class PrecedenceActivity extends NavigationDrawerActivity with TypedFindView {
    var mRecyclerView: RecyclerView = null
    var mAdapter: PrecedenceAdapter = null

    override def onCreate( savedInstanceState: Bundle ): Unit = {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.precedence_activity)

        val myToolbar = findView( TR.my_toolbar )
        setSupportActionBar( myToolbar )

        mRecyclerView = findView(TR.lpoPrecContainer)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this))
        mRecyclerView.setAdapter(new PrecedenceAdapter(Controller.state.precedence))
        mRecyclerView.setHasFixedSize(true)

    }
}
