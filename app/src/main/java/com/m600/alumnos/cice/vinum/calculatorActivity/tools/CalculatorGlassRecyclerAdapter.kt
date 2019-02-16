package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.m600.alumnos.cice.vinum.R
import kotlinx.android.synthetic.main.calculator_glass_card.view.*

class CalculatorGlassRecyclerAdapter(val context: Context): RecyclerView.Adapter<CalculatorGlassRecyclerAdapter.CalculatorViewHolder>() {

    private var listOfViewHolders = HashMap<Int,CalculatorViewHolder>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CalculatorViewHolder {
        val card = LayoutInflater.from(context).inflate(R.layout.calculator_glass_card,p0,false)
        return CalculatorViewHolder(card)
    }

    override fun getItemCount(): Int {
        return 29
    }

    override fun onBindViewHolder(p0: CalculatorViewHolder, p1: Int) {
        listOfViewHolders.putIfAbsent(p1,p0)
    }

    override fun onViewRecycled(holder: CalculatorViewHolder) {
       super.onViewRecycled(holder)
        CalculatorGlassAnimationController.syncWithFrame(holder.image)

    }

    //Sincroniza las animaciones de todas las copas
    fun updateAnimations(){
        listOfViewHolders.forEach { t, u ->
            CalculatorGlassAnimationController.syncWithFrame(u.image)
        }
    }

    //Comprueba si la copa actualmente visible esta siendo animada
    fun isCurrentPositionAnimationPlaying(position: Int): Boolean{
        return listOfViewHolders[position]!!.image.isAnimating
    }


    //Pausa o cancela todas las animaciones
    fun cancelAnimations(destroy: Boolean){
        if (destroy){
            listOfViewHolders.forEach { t, u ->
                u.image.cancelAnimation()
            }
        } else {
            listOfViewHolders.forEach { t, u ->
                u.image.pauseAnimation()
            }
        }
    }

    class CalculatorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image = itemView.calculatorGlassCardImage
    }
}