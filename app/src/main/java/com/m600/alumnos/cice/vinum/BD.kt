package com.m600.alumnos.cice.vinum

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import java.net.URL
import java.util.*
import org.json.JSONArray





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
     * tiene los atributos obligatorios, este devolverá un PAIR compuesto por un String que será
     * el mensaje del proceso y un boolean para confirmar si el proceso se ha completado o ha ocurrido un error.
     *
     *
     * [Elementos Obligatorios para guardar un vino]
     * - Nombre
     * - Origen
     * - Año
     * - Descripcion
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


    fun guardarVino(FirebaseBD: DatabaseReference, vino: Vino):Pair<String, Boolean>{

        //Obtiene el año actual para comprobar que no se meta un año imposible
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val resultado: Pair<String,Boolean>

        if(vino.nombre!!.isBlank()){

            Log.i("VINUMLOG","El nombre no puede estar vacío")
            resultado = Pair("El nombre no puede estar vacío",false)

        }else if(comprobarVinoDuplicado(vino.nombre!!)){

            Log.i("VINUMLOG","El vino introducido ya existe")
            resultado = Pair("El vino introducido ya existe",false)

        }else if(vino.origen!!.isBlank()){

            Log.i("VINUMLOG","El origen no puede estar vacío")
            resultado = Pair("El origen no puede estar vacío",false)

        }else if(vino.anio == null || (vino.anio!!<1500 || vino.anio!!>currentYear)){

            Log.i("VINUMLOG","El año introducido es incorrecto")
            resultado = Pair("El año introducido es incorrecto",false)

        }else if(vino.descripcion!!.isBlank()){

            Log.i("VINUMLOG","La descripcion no puede estar vacía")
            resultado = Pair("La descripcion no puede estar vacía",false)

        }else if(vino.bodega!!.isBlank()){

            Log.i("VINUMLOG","La bodega no puede estar vacía")
            resultado = Pair("La bodega no puede estar vacía ",false)

        }else{

            //Lo obtenemos de la base de datos
            val identificadorTarea = FirebaseBD.child("vinos").push()

            //Enviamos el vino a traves del id
            identificadorTarea.setValue(vino)
            Log.i("VINUMLOG","Se ha insertado correctamente")

            resultado = Pair("Se ha insertado el vino con éxito",false)

            return resultado

        }

        return resultado
    }


    /**
     *
     * Se puede hacer mas eficiente
     */

    private fun comprobarVinoDuplicado(nombre: String):Boolean{

        val peticionBD = URL("https://vinum-9b17f.firebaseio.com/vinos.json").readText()

        var duplicado = false

        if(peticionBD.contains(nombre, ignoreCase = true)){

            duplicado = true

        }

        return duplicado
    }






}