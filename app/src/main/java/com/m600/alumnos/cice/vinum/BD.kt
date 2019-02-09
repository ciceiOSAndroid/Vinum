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

        // Se declara e incializar el array encargado de almacenar los objeto << Vino >>
        val vinoObjects:ArrayList<Vino> ?= arrayListOf()

        val vinosFirebase = dataSnapshot.children.iterator()
        if(vinosFirebase.hasNext()){

            val listaIndex = vinosFirebase.next()
            val itemsIterator = listaIndex.children.iterator()
            while (itemsIterator.hasNext()){ //Recorre todos los vinos

                //Obtenemos la información de ellos

                val vinoActual = itemsIterator.next()
                val vino = Vino()

                val map = vinoActual.value as HashMap<String, Any>

                vino.ID = vinoActual.key
                vino.nombre = map["nombre"] as String
                vino.anio = map["año"] as Long
                vino.uva = map["uva"] as String
                vino.grados = map["grados"] as String
                vino.origen = map["origen"] as String
                vino.bodega = map["bodega"] as String
                vino.descripcion = map["descripcion"] as String
                vino.imagen = map["imagen"] as String //URL

                /* El vino puede no tener puntuaciones por lo que su atributo puede permanecer declarado
                por defecto como null */
                if(map["puntuaciones"]!=null){
                    vino.puntuaciones = map["puntuaciones"] as HashMap<String, Int>
                }


                //Se añade el objeto Vino con sus propiedades
                vinoObjects!!.add(vino)
            }

        }

        return vinoObjects
    }



}