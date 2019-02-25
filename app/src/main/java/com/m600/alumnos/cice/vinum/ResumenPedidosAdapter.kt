package com.m600.alumnos.cice.vinum

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.cardview_resumenpedidos.view.*

class ResumenPedidosAdapter(private val context: Context, private val lista: ArrayList<Vino>, val clickListener: AdapterClickListener)
    : RecyclerView.Adapter<ResumenPedidosAdapter.ViewHolder>() {

    //interfaz para coger el número de niveles declarados en el MainActivity
    interface  AdapterClickListener{
        fun refreshList()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            : ResumenPedidosAdapter.ViewHolder {

        val vistaCelda = LayoutInflater.from(context).inflate(R.layout.cardview_resumenpedidos, p0, false)
        return ViewHolder(vistaCelda)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(p0: ResumenPedidosAdapter.ViewHolder, p1: Int) {
        p0.bindItem(lista[p1])
        p0.itemView.trashButton.setOnClickListener {
            //Borra vino
            ListDataManager(context).deleteVino(p0.itemView.vinoTitle.text.toString())
            clickListener.refreshList()
            Log.i("ResumenPedidos", "BORRA")
        }


    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        //interfaz para refrescar lista en el MainActivity
        interface  ViewHolderClickListener{
            fun refreshList()
        }

        fun bindItem(vino: Vino) {
            var nombre = itemView.vinoTitle
            var descripcion = itemView.vinoDescription
            var precio = itemView.vinoPrice
            var trashButton = itemView.trashButton

            nombre.text = vino.name
            descripcion.text = vino.features[1]
            precio.text = vino.features[0]

        }
    }
}

