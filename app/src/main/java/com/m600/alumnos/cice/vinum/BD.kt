package com.m600.alumnos.cice.vinum

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


open class BD {

    // Se declara e incializa el array encargado de almacenar los objetos << Vino >>
    val vinoObjects:ArrayList<Vino2> ?= arrayListOf()

    // Almacenará la URL que referencia a la foto que se acaba de subir
    var urlImagen:String ?= null

    // Numero de vinos de la base de datos
    var numeroVinos: Long ?= null


    /**
     * Método que va en << onDataChange >> encargado de generar los objetos << Vino >>
     * a partir de la petición a FIREBASE que va a recibir.
     *
     * @param  DataSnapshot
     * @return ArrayList<Vino>?
     * @author K3V1NS4N
     *
     */
    fun cargarVinos(firebaseStorage: FirebaseStorage, dataSnapshot: DataSnapshot){

        vinoObjects?.clear() // Se borran los vinos almacenados

        val vinosFirebase = dataSnapshot.children.iterator()



        if(vinosFirebase.hasNext()){


            val listaIndex = vinosFirebase.next()
            val itemsIterator = listaIndex.children.iterator()

            //Obtiene el numero de vinos que hay en la base de datos
            numeroVinos = listaIndex.childrenCount


                while (itemsIterator.hasNext()) { //Recorre todos los vinos


                    //Obtenemos la información de ellos

                    val vinoActual = itemsIterator.next()
                    val vino = Vino2()

                    val map = vinoActual.value as HashMap<String, Any>


                    //Se añade el objeto Vino con sus propiedades


                    Log.i("VINUM", "${vino.anio}")
                    //vino.ID = vinoActual.key
                    vino.nombre = map["nombre"] as String
                    vino.origen = map["origen"] as String
                    vino.bodega = map["bodega"] as String
                    vino.descripcion = map["descripcion"] as String
                    vino.uva = map["uva"] as String

                    if (map["año"] != null) {

                        vino.anio = map["año"] as Long
                    }

                    if (map["grados"] != null) {

                        vino.grados = map["grados"] as String

                    } else {

                        vino.grados = "?"
                    }


                    /* El vino puede no tener puntuaciones por lo que su atributo puede permanecer declarado
                         por defecto como null */
                    if (map["puntuacion"] != null) {
                        vino.puntuacion = map["puntuacion"] as Long
                    }


                    if(map["imagenUrl"] != null){

                        //[START FOTOS ]
                        val URl = map["imagenUrl"] as String
                        // Creación de una referencia a un archivo desde una URI de almacenamiento en la nube de Google
                        val gsReference = firebaseStorage.getReferenceFromUrl(URl)


                        Log.i("VINUMLOG", URl)
                        //Resolucion de la foto
                        val ONE_MEGABYTE: Long = 1024 * 1024

                        //Esto se ejecuta de manera asíncrona
                        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                            // Obtiene la informacion de la foto en un ByteArray

                            //Decodificamos el ByteArray en un Bitmap para poder insertarlo en una ImageView
                            vino.imagen = BitmapFactory.decodeByteArray(it, 0, it.size)

                            Log.i("VINUMLOG", "INSERTADO ${vino.imagen} ")
                            Log.i("VINUMLOG", "SE AÑADE EL VINO")
                            vinoObjects!!.add(vino)

                        }.addOnFailureListener {
                            // Handle any errors

                            Log.i("VINUMLOG", "ERROR AL OBTENER LA FOTO $URl")

                        }

                        //[END FOTOS ]


                    }


                }



            }

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
     * @author K3V1NS4N
     */


    fun guardarVino(FirebaseBD: DatabaseReference, vino: Vino2):Pair<String, Boolean>{

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

        }else if(vino.imagenUrl!!.isBlank()){

            Log.i("VINUMLOG","El vino necesita una imagen")
            resultado = Pair("El vino necesita una imagen",false)

        }else{

            //Lo obtenemos de la base de datos
            val identificadorTarea = FirebaseBD.child("vinos").push()

            //Enviamos el vino a traves del id
            identificadorTarea.setValue(vino)
            Log.i("VINUMLOG","Se ha insertado correctamente")

            resultado = Pair("Se ha insertado el vino con éxito",true)

            return resultado

        }

        return resultado
    }


    /**
     *  Metodo encargado de comprobar que el nombre del vino a insertar no existe ya en la Base de Datos.
     * @param nombre String
     * @return Boolean
     * @author K3V1NS4N
     */

    private fun comprobarVinoDuplicado(nombre: String):Boolean{

        //Obtiene el json RAW de la base de datos
        val peticionBD = URL("https://vinum-9b17f.firebaseio.com/vinos.json").readText()

        var duplicado = false

        //Comprueba si el nombre del vino ya existe dentro de la base de datos
        if(peticionBD.contains(nombre, ignoreCase = true)){

            duplicado = true

        }

        return duplicado
    }



    /**
     *  Se encarga de subir la fotografía del vino, pasándole una ImageView, se recomienda que sea
     *  el ImageView de previsualización de la foto que se ha hecho del vino.
     * @param firebaseStorage String
     * @param imagen ImageView
     * @param nombre String
     * @return Boolean
     * @author K3V1NS4N
     */
     fun subirFoto(firebaseStorage: FirebaseStorage, imagen: ImageView, nombre: String){

        // [START REFERENCIA DE LA SUBIDA]
        // Create a storage reference from our app
        val storageRef = firebaseStorage.reference

        // Crea una referencia de la foto con este nombre "mountains.jpg"
        val mountainsRef = storageRef.child("$nombre.png")

        // [END REFERENCIA DE LA SUBIDA]

        // [START SUBIR FOTO]

        // Obtener los datos de un ImageView como bytes
         imagen.isDrawingCacheEnabled = true
         imagen.buildDrawingCache()
        val bitmap = (imagen.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Ha ocurrido un error al subir la foto

        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

            urlImagen = it.storage.toString()

            Log.i("VINUMLOG","REFERENCIA: ${it.storage}")

        }

        // [END SUBIR FOTO]

    }

}