package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*


/**
 * DATABASE
 * Cada parte del código tiene un identificador que hace necesario incluirlo
 * dependiendo si se quiere obtener o subir vinos a FIREBASE.
 *
 * Aquellos que estan marcados con [EJ] son ejemplos y no es necesario incluirlos tal como estan para el funcionamiento
 * correcto de la base de datos.
 *
 *  [1] -> OBTENER VINOS
 *  [2] -> SUBIR VINOS
 *  [EJ] -> Ejemplo
 */


class ejemplosBDFirebase : AppCompatActivity() {

    // [-----------------<| BD FIREBASE |>-----------------] //

    //Se declara la referencia a la base de datos
    private lateinit var FirebaseBD: DatabaseReference // BD FIREBASE

    /**[1] & [2]**/
    //Instancia de la clase BD necesaria para obtener y subir vinos
    lateinit var BD_vinos: BD

    /**[1] & [2]**/
    //Instancia de firebase necesaria para subir y descargar las fotos
    val storage = FirebaseStorage.getInstance()



    /**[1]**/
    //Se declara el listener y sus respectivos metodos encargados de obtener los vinos
    var taskListener: ValueEventListener = object : ValueEventListener {

        override fun onCancelled(p0: DatabaseError) {

            //Nothing
        }


        // Se ejecutara cada vez que se llame a este activity o haya un cambio en la BD
        override fun onDataChange(snapshot: DataSnapshot) {


            /**[1]**/ // LLama el metodo cargar vinos para que los vaya cargando
            BD_vinos.cargarVinos(storage, snapshot)

            /**[1]**/
            //Esta variable es una forma de contar las comprobaciones al recurso vinoObjects:ArrayList<Vino>
            // de la clase BD ya que hay cierto delay al cargar las fotos y generar los objetos vino.
            var intento = 0

            /**[1]**/ //Hilo
            Thread{
                /**[1]**/ // hará 40 comprobaciones cada 300m hasta que se hayan cargado todos los vinos.
                while(intento!=30){

                    /**[1]**/ //Accede al array de objetos vinos de la clase BD
                    val vinos  = BD_vinos.vinoObjects

                    /**[1]**/ //Duerme el hilo 200 ms para darle tiempo a FIREBASE
                    Thread.sleep(300)

                    Log.i("HOLA", "Vinos cargados: ${vinos?.size} Vinos FIREBASE: ${BD_vinos.numeroVinos}")
                    /**[1]**/ // Se comprueba si se han obtenido todos los vinos de FIREBASE
                    if((vinos!= null) && (vinos.size.toLong() == BD_vinos.numeroVinos)){

                        /**[EJ]**/ //Se recorren los objetos obtenidos para mostrarlos
                        for (item in vinos) {

                            /**[EJ]**/ //Se muestran a modo de prueba
                            Log.i("VINUMLOG", "Nombre: ${item.nombre}\n" +
                                        "anio: ${item.anio} \n" +
                                        "bodega: ${item.bodega}\n" +
                                        "uva: ${item.uva}\n" +
                                        "origen: ${item.origen}\n" +
                                        "puntuación: ${item.puntuacion}\n" +
                                        "descripcion: ${item.descripcion}\n")

                            /**[EJ]**/
                            Log.i("VINUMLOG", item.imagen.toString())

                            /**[1] - [EJ]**/
                            // Ejemplo para mostrar en una ImageView la imagen obtenida de FIREBASE
                            // vinculada a su objeto.
                            runOnUiThread {
                                // Stuff that updates the UI
                                imageView.setImageBitmap(item.imagen)

                            }
                            /**[EJ]**/ //Solo sirve para que de tiempo a ver las fotos mostrarse
                            Thread.sleep(150)


                        }
                        /**[1]**/ // Se sale del bucle while por que se han obtenido los vinos a tiempo.
                        intento = 30

                        /**[1]**/ //En caso de que no se hayan obtenido aun los vinos de FIREBASE
                    }else{

                        /**[1]**/
                        intento++

                        /**[EJ]**/ //Comprueba si el numero de intentos ha terminado
                        if(intento==30){

                            /**[EJ]**/ //En caso de no obtener los vinos por cualquier motivo,
                            //por ejemplo que no tengas internet o una conexion lenta.
                            runOnUiThread {

                                Toast.makeText(this@ejemplosBDFirebase,
                                    "No se han podido cargar los vinos, revisa tu conexión a internet o intentalo de nuevo.",
                                    Toast.LENGTH_LONG).show()
                            }

                        }

                    }


                    /**[EJ]**/
                    runOnUiThread {
                        numVinos.text = vinos!!.size.toString()
                    }

                }


            }.start() /**[1]**/ //Comienza el hilo

        }
    }
    // [---------------------------------------------------] //



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) /**[1] & [2]**/
        setContentView(R.layout.activity_main)

        // [-----------------<| BD FIREBASE |>-----------------] //
        /**[1]**/
        FirebaseBD = FirebaseDatabase.getInstance().reference //Se obtiene la referencia
        /**[1]**/
        FirebaseBD.orderByKey().addValueEventListener(taskListener)
        /**[1 & 2]**/
        BD_vinos = BD()



        // [---------------------------------------------------] //


    }


    /**
     *  /**[EJ]**/
     *  Prueba para usar el metodo añadirVinos al pulsar un boton
     */

    fun añadirVinos(view: View){


        if(imageView!=null){

            /**[2]**/
            //Se llama al método encargado de subir las fotografias pasandole la imagen obtenida anteriormente de la BD.
            //[Se le debe pasar un imageView] por ejemplo la imagen de previsualización de la foto del vino a subir.
            BD_vinos.subirFoto(storage,imageView,"foto1")

            /**[2]**/
            //Esta variable es una forma de contar las comprobaciones al recurso urlImagen
            //de la clase BD ya que hay cierto delay al subir la foto y obtener de vuelta su URL para poder almacenar el vino.
            var intento = 0

            Thread{
                /**[2]**/ // hará 30 comprobaciones cada 200m hasta que se haya subido la imagen
                while (intento!=30){

                    /**[EJ]**/
                    Log.i("VINUMLOG","IMAGEN URL BD: ${BD_vinos.urlImagen}")

                    /**[2]**/ // Duerme el hilo
                    Thread.sleep(200)

                    /**[2]**/ //Comprueba si se ha subido la imagen
                    if(BD_vinos.urlImagen==null){

                        intento++

                        /**[EJ]**/ //Comprueba si el numero de intentos ha terminado
                        if(intento==30){

                            /**[EJ]**/ //En caso de no haber podido subir la foto a tiempo al acabarse
                            //el numero de comprobaciones al recurso por cualquier motivo,
                            //por ejemplo que no tengas internet o una conexion lenta.
                            runOnUiThread {


                                Toast.makeText(this@ejemplosBDFirebase,
                                    "No se ha podido subir la foto intentalo de nuevo",
                                    Toast.LENGTH_LONG).show()
                            }

                        }



                    }else{ //Se ha subido la imagen

                        //Ejmplo de subida de un vino se podra gestionar como se desee:

                        /**[2] - [EJ]**/ // Se declara un nuevo vino
                        val vino = Vino2()

                        /**[2] - [EJ]**/ //Se añaden al menos sus elementos obligatorios
                        vino.nombre = nombreVino.text.toString()
                        vino.anio = 2000
                        vino.bodega = "Prueba"
                        vino.uva = "Prueba"
                        vino.origen = "Prueba"
                        vino.puntuacion = 5
                        vino.descripcion = "Prueba"
                        vino.imagenUrl = BD_vinos.urlImagen

                        /**[2]**/ //Llama el metodo encargado de almacenar el vino
                        //Devolverá el resultado String <Mensaje>, Boolean <Satisfactorio o no>
                        val resultado = BD_vinos.guardarVino(FirebaseBD,vino)

                        /**[2]**/ // Se sale del bucle while por que se han obtenido los vinos a tiempo.
                        intento = 30

                        /**[EJ]**/ //El resultado se podra gestionar como se desee
                        if(resultado.second){

                            runOnUiThread {
                                //Si al añadir un vino este se añade satisfactoriamente
                                Toast.makeText(this@ejemplosBDFirebase,
                                    resultado.first,Toast.LENGTH_LONG).show()

                            }

                        }else{

                            runOnUiThread {
                                //Si al añadir un vino ha ocurrido algun error
                                Toast.makeText(this@ejemplosBDFirebase,
                                    resultado.first,Toast.LENGTH_LONG).show()
                            }
                        }

                    }


                }
            }.start() /**[2]**/ //Comienza el hilo

        }
    }
}
