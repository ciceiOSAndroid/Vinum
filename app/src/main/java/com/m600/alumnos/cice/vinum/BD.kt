package com.m600.alumnos.cice.vinum

import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.net.URL
import java.util.*

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

        // Se declara e incializar el array encargado de almacenar los objetos << Vino >>
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

                Log.i("VINUM","${vino.anio}")
                //vino.ID = vinoActual.key
                vino.nombre = map["nombre"] as String
                vino.origen = map["origen"] as String
                vino.bodega = map["bodega"] as String
                vino.descripcion = map["descripcion"] as String
                vino.uva = map["uva"] as String

                if(map["año"]!=null){

                    vino.anio = map["año"] as Long
                }

                if(map["grados"]!=null) {

                    vino.grados = map["grados"] as String

                }else{

                    vino.grados = "?"
                }

                if(map["imagen"]!=null) {

                    vino.imagen = map["imagen"] as String //URL

                }else {

                    //No hay imagen

                }

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


    /**
     * Se encarga de guardar los vinos en la base de datos, tambien comprueba que el vino a insertar
     * tiene los atributos obligatorios por lo que devolverá PAIR  si el vino se ha insertado correctamente
     * o FALSE si ha ocurrido algún error, [Utiliza el TAG "VINUMLOG" para ver que esta ocurriendo]
     *
     * [Elementos Obligatorios para guardar un vino]
     * - Nombre
     * - Origen
     * - Año
     * - Descripcion
     * - Origen
     * - Bodega
     * [Elementos NO Obligatorios para guardar un vino]
     * - Uva
     * - Grados
     * - IMAGEN (URL)
     * - Puntuaciones
     *
     * @param FirebaseBD DatabaseReference
     * @param vino Vino
     * @return PAIR<String,Boolean>
     *
     */


    fun guardarVino(FirebaseBD: DatabaseReference, vino: Vino):Boolean{

        //Obtiene el año actual para comprobar que no se meta un año imposible
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        if(vino.nombre!!.isBlank()){

            Log.i("VINUMLOG","El nombre no puede estar vacio")

        }else if(vino.origen!!.isBlank()){

            Log.i("VINUMLOG","El origen no puede estar vacio")

        }else if(vino.anio == null || (vino.anio!!<1500 || vino.anio!!>currentYear)){

            Log.i("VINUMLOG","El año no puede estar vacio o es incorrecto")

        }else if(vino.descripcion!!.isBlank()){

            Log.i("VINUMLOG","La descripcion no puede estar vacia")

        }else if(vino.origen!!.isBlank()){

            Log.i("VINUMLOG","El origen no puede estar vacio")

        }else if(vino.bodega!!.isBlank()){

            Log.i("VINUMLOG","La bodega no puede estar vacia")

        }else{

            //Lo obtenemos de la base de datos
            val identificadorTarea = FirebaseBD.child("vinos").push()

            //Enviamos el vino a traves del id
            identificadorTarea.setValue(vino)
            Log.i("VINUMLOG","Se ha insertado correctamente")

            return true

        }

        Log.i("VINUMLOG","error")

        return false
    }



    private fun comprobarVinoDuplicado(FirebaseBD: DatabaseReference, vino: Vino):Boolean{


        val a = URL("https://${R.string.PROJECT_ID}.firebaseio.com/${R.string.FIREBASE_VINO}").readText()


        return true
    }






}