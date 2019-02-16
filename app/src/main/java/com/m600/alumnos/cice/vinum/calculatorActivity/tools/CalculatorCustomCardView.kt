package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CalculatorCustomCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr : Int = 0):
    CardView(context,attributeSet,defStyleAttr){

    private var radiusComputed = 0f
    private var radiusSet = false

    //Aqui se auto implementa la altura de la cardview de las copas en funcion de la altura, tambien se fija el radio de sus bordes
    //Correcto para que siga siendo un circulo
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        layoutParams.height = View.MeasureSpec.getSize(widthMeasureSpec)
        radius = (MeasureSpec.getSize(widthMeasureSpec) / 2).toFloat()
    }

    override fun setRadius(radius: Float) {
        super.setRadius(radius)
    }

}