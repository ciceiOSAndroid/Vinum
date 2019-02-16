package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView

class CalculatorGlassSnapOnScrollListener (
    private val snapHelper: LinearSnapHelper,
    val layoutManager: LinearLayoutManager,
    var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    private var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    var directionRight = false

    interface OnSnapPositionChangeListener {

        fun sendScrollBehaviour(scrolling: Boolean, right: Boolean)
        fun onSnapPositionChange(position: Int)
    }

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    //Detecta y envia cuando el scroll esta moviendose
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }

        directionRight = dx > 0
        if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_DRAGGING){
            onSnapPositionChangeListener?.sendScrollBehaviour(true,directionRight)
        }
    }

    //Detecta y envia cuando el scroll cambia de estado, de parado a Idle son los utilizados principalmente
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }

        if (newState == RecyclerView.SCROLL_STATE_IDLE){
            onSnapPositionChangeListener?.sendScrollBehaviour(false, directionRight)
        }
    }

    //Envia la posicion del elemento visible actualmente
    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
        val snapPosition = layoutManager.getPosition(snapView!!)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}
