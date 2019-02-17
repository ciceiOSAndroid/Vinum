package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView

class CalculatorPesoSnapOnScrollListener (
    private val snapHelper: LinearSnapHelper,
    val layoutManager: LinearLayoutManager,
    var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    private var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    interface OnSnapPositionChangeListener {
        fun onSnapPesoPositionChange(position: Int)
    }

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
        val snapPosition = layoutManager.getPosition(snapView!!)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPesoPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}