package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.m600.alumnos.cice.vinum.R
import kotlinx.android.synthetic.main.calculator_peso_card.view.*

class CalculatorPesoRecyclerAdapter(val context: Context): RecyclerView.Adapter<CalculatorPesoRecyclerAdapter.CalculatorPesoViewHolder>()  {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CalculatorPesoViewHolder {
        val card = LayoutInflater.from(context).inflate(R.layout.calculator_peso_card,p0,false)
        return CalculatorPesoViewHolder(card)
    }

    private val selectedView: CalculatorPesoViewHolder? = null

    override fun getItemCount(): Int {
        return  100
    }

    override fun onBindViewHolder(p0: CalculatorPesoViewHolder, p1: Int) {
        p0.texto.text = (p1 + 40).toString()
    }

    class CalculatorPesoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val texto = itemView.calculatorPesoCounter
    }
}