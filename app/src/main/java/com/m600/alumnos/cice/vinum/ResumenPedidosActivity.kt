package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_resumen_pedidos.*
import com.google.gson.Gson
import android.widget.Toast
import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import java.util.*


class ResumenPedidosActivity : AppCompatActivity(), ResumenPedidosAdapter.AdapterClickListener {


    var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: ResumenPedidosAdapter? = null

    //Data Source from SP
    var vinos: ArrayList<Vino>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_pedidos)

        rellenarVinos()

    }

    private fun rellenarVinos() {
        //DataSource
        vinos = ListDataManager(this).getLists()

        //Comprobamos si el SP está vacío, si no rellenamos el RecyclerView
        if(vinos == null || vinos!!.isEmpty()){
            Log.i("ResumenPedidos", "vinos = null o vinos vacío")
            rVPedidos.visibility = View.INVISIBLE
            emptyVinoImg.visibility = View.VISIBLE
            cestaProducts.text= "No hay vinos"
        } else  {
            Log.i("ResumenPedidos", "vinos != null")
            rVPedidos.visibility = View.VISIBLE
            emptyVinoImg.visibility = View.INVISIBLE
            //Inicializamos
            layoutManager = LinearLayoutManager(this)
            adapter = ResumenPedidosAdapter(this, vinos!!, this)
            cestaProducts.text= "${vinos?.size} vinos"
            //Asignamos al RecyclerView
            rVPedidos.layoutManager = layoutManager
            rVPedidos.adapter = adapter
        }
    }

    //Añade Vino de ejemplo para que funciona el código Pablo :)
    fun addFakeVino(view: View) {
        val features: ArrayList<String> = arrayListOf<String>()
        features.add("Un buen vino de la cosecha del 1994")
        features.add("12 euros")
        val name = randomNameGenerator()

        //Vino de ejemplo
        val Vino = Vino(name, features)
        ListDataManager(this).saveList(Vino)
        rellenarVinos()
    }

    fun randomNameGenerator(): String {
        val generator = Random()
        val randomStringBuilder = StringBuilder()
        val randomLength = generator.nextInt(5)
        var tempChar: Char
        for (i in 0 until randomLength) {
            tempChar = (generator.nextInt(96) + 32).toChar()
            randomStringBuilder.append(tempChar)
        }
        return randomStringBuilder.toString()
    }

    override fun refreshList() {
        rellenarVinos()
    }
}
