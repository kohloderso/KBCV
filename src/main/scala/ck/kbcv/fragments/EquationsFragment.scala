package ck.kbcv.fragments

import android.graphics.{Canvas, Color}
import android.graphics.drawable.{ColorDrawable, Drawable}
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView.{ItemDecoration, ViewHolder}
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.Log
import android.view._
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.adapters.EquationsAdapter
import ck.kbcv.adapters.EquationsAdapter.ItemClickListener
import ck.kbcv.views.EquationView
import ck.kbcv.{Controller, R}
import term.parser.ParserXmlTRS

import scala.collection.mutable.ListBuffer


class EquationsFragment extends Fragment with ItemClickListener {
    val TAG = "EquationsFragment"
    var mEquationsRV: RecyclerView = null
    var mAdapter: EquationsAdapter = null
    var mActionMode: ActionMode = null
    var mActionModeCallback = new ActionModeCallback

    override def onCreateView( inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle ): View = {
        val view = inflater.inflate( R.layout.equations_fragment, container, false )
        mEquationsRV = view.findViewById(R.id.rvEquations).asInstanceOf[RecyclerView]

        // remove this after testing!!
        if(Controller.state.equations.isEmpty) {
            val stream = getActivity.openFileInput("new")
            val es = ParserXmlTRS.parse(stream)
            Controller.setES(es)
        }
        mAdapter = new EquationsAdapter(Controller.state.equations.toBuffer, this)
        mEquationsRV.setAdapter(mAdapter)
        mEquationsRV.setLayoutManager(new LinearLayoutManager(getActivity))
        mEquationsRV.setHasFixedSize(true)   // if every item has the same size, use this for better performance

        new ItemTouchHelper(new EquationTouchHelperCallback).attachToRecyclerView(mEquationsRV)
        return view
    }

    override def onItemClicked(position: Int): Unit = {
        if(mActionMode == null) {
            mActionMode = getActivity.startActionMode(mActionModeCallback)
        }
        toggleSelection(position)
    }

    override def onItemLongClicked(position: Int): Unit = {
        if(mActionMode == null) {
            mActionMode = getActivity.startActionMode(mActionModeCallback)
        }
        toggleSelection(position)
    }

    def toggleSelection(position: Int): Unit = {
        mAdapter.toggleSelection(position)

        val count = mAdapter.selectedIndices.size
        if(count == 0) {
            mActionMode.finish()
        } else {
            mActionMode.setTitle(count.toString)
            mActionMode.invalidate()
        }
    }

    class EquationTouchHelperCallback() extends ItemTouchHelper.Callback {

        override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
            val position = viewHolder.getAdapterPosition
            if(mAdapter.isPendingRemoval(position)) return 0
            return ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT )
        }

        override def isLongPressDragEnabled: Boolean = false

        override def isItemViewSwipeEnabled: Boolean = true

        override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = ???

        override def onSwiped(viewHolder: ViewHolder, direction: Int): Unit = {
            direction match {
                case ItemTouchHelper.LEFT => mAdapter.pendingRemoval(viewHolder.getAdapterPosition)//orientRL(viewHolder)
                case ItemTouchHelper.RIGHT => mAdapter.pendingRemoval(viewHolder.getAdapterPosition)
                case _ =>
            }
        }
    }

    /**
     * see here for detailed infos: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/blob/master/app/src/main/java/net/nemanjakovacevic/recyclerviewswipetodelete/MainActivity.java
     */
    def setUpAnimationDecorator(): Unit = {
        mEquationsRV.addItemDecoration(new ItemDecoration {
            // we want to cache this and not allocate anything repeatedly in the onDraw method
            val background: Drawable = new ColorDrawable(Color.RED)

            override def onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State): Unit = {
                // only if animation is in progress
                if(parent.getItemAnimator.isRunning()) {
                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    var lastViewComingDown: View = null
                    var firstViewComingUp: View = null

                    // this is fixed
                    val left = 0
                    val right = parent.getWidth

                    // this we need to find out
                    var top = 0
                    var bottom = 0

                    // find relevant translating views
                    val childCount = parent.getLayoutManager.getChildCount
                    for(i <- 0 until childCount) {
                        val child = parent.getLayoutManager.getChildAt(i)
                        if(child.getTranslationY < 0) {
                            // view is coming down
                            lastViewComingDown = child
                        } else if(child.getTranslationY > 0) {
                            // view is coming up
                            if(firstViewComingUp == null) {
                                firstViewComingUp = child
                            }
                        }
                    }

                    if(lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom + lastViewComingDown.getTranslationY.toInt
                        bottom = firstViewComingUp.getTop + firstViewComingUp.getTranslationY.toInt
                    } else if(lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom + lastViewComingDown.getTranslationY.toInt
                        bottom = lastViewComingDown.getBottom
                    } else if(firstViewComingUp != null) {
                        // views are going up to fill the void
                        top = firstViewComingUp.getTop
                        bottom = firstViewComingUp.getTop + firstViewComingUp.getTranslationY.toInt
                    }

                    background.setBounds(left, top, right, bottom)
                    background.draw(canvas)
                }
                super.onDraw(canvas, parent, state)
            }
        })
    }


    class ActionModeCallback extends ActionMode.Callback {
        private val TAG = "ACTIONMODE_CB"

        override def onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            actionMode.getMenuInflater.inflate(R.menu.selected_equation_menu, menu)
            true
        }

        override def onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = {
            false
        }

        override def onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean = {
            item.getItemId match {
                case R.id.action_orientLR =>
                    Log.d(TAG, "orientLR")
                    // TODO
                    actionMode.finish()
                    true
                case R.id.action_orientRL =>
                    Log.d(TAG, "orientRL")
                    // TODO
                    actionMode.finish()
                    true
                case R.id.action_simplify =>
                    Log.d(TAG, "simplify")
                    // TODO
                    actionMode.finish()
                    true
                case R.id.action_delete =>
                    Log.d(TAG, "delete")
                    // TODO
                    actionMode.finish()
                    true
                case _ => false
            }
        }

        override def onDestroyActionMode(actionMode: ActionMode): Unit = {
            mAdapter.clearSelection()
        }

    }


}