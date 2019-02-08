package com.m600.alumnos.cice.vinum

import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class BD {


    /**
     * Método que va en << onDataChange >> encargado de generar los objetos << Vino >>
     * a partir de la petición a FIREBASE que va a recibir.
     *
     * @param  DataSnapshot
     * @return ArrayList<Vino>?
     *
     */

    fun cargarVinos(dataSnapshot: DataSnapshot):ArrayList<Vino>?{

        var vinos:ArrayList<Vino> ?= null

        val tareas = dataSnapshot.children.iterator()
        if(tareas.hasNext()){

            val listaIndex = tareas.next()
            val itemsIterator = listaIndex.children.iterator()
            while (itemsIterator.hasNext()){

                //Obtenemos la información

                val tareaActual = itemsIterator.next()
                val vino = Vino()

                val map = tareaActual.value as HashMap<String, Any>
                vino.done = map["done"] as Boolean
                vino.taskDesc = map["taskDesc"] as String
                vino.objectId = tareaActual.key

                vinos!!.add(vino)
            }

        }

        return vinos
    }



}