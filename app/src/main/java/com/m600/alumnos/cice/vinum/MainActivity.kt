package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import android.os.StrictMode
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {



    // [-----------------<| BD FIREBASE |>-----------------] //

    //Se declara la referencia a la base de datos
    private lateinit var FirebaseBD: DatabaseReference // BD FIREBASE

    lateinit var BD_vinos: BD // Gestor de la BD

    //storage_field_declaration
    val storage = FirebaseStorage.getInstance()

    var imagenUrl = ""

    var taskListener: ValueEventListener = object : ValueEventListener {


        override fun onCancelled(p0: DatabaseError) {

            //Nothing
        }


        // [-----------------<| PRUEBA MOSTRAR VINOS |>-----------------] /
        override fun onDataChange(p0: DataSnapshot) {



            // Devuelve todos los vinos como ArrayList<Vino>?

            BD_vinos.cargarVinos(storage, p0)





            var obtenido = 0

            Thread{

                while(obtenido!=25){

                    //Log.i("VINUMLOG","COMIENZA EL HILO")

                    val resultado = BD_vinos.comprobarImagenes()
                    Thread.sleep(200)

                    if((resultado!= null) && (resultado.size == 6)){

                        //Se recorren los resultados y se muestran
                        for (item in resultado!! ) {

                            Log.i(
                                "VINUMLOG", "Nombre: ${item.nombre}\n" +
                                        "anio: ${item.anio} \n" +
                                        "bodega: ${item.bodega}\n" +
                                        "uva: ${item.uva}\n" +
                                        "origen: ${item.origen}\n" +
                                        "descripcion: ${item.descripcion}\n"
                            )

                            // [START MOSTRAR FOTOS]


                            Log.i("VINUMLOG", "SE MUESTRA")
                            Log.i("VINUMLOG", item.imagen.toString())

                            runOnUiThread {
                                // Stuff that updates the UI
                                imageView.setImageBitmap(item.imagen)

                            }


                            // [END MOSTRAR FOTOS]

                            obtenido = 25
                        }

                    }else{

                            obtenido++

                        if(obtenido==25){

                            runOnUiThread {
                                // Stuff that updates the UI
                                Toast.makeText(this@MainActivity,"No se han podido cargar los vinos, revisa tu conexión a internet",Toast.LENGTH_LONG).show()
                            }

                        }

                    }

                }


            }.start()


        }
    }
    // [---------------------------------------------------] //



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) //BD FIREBASE
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)


        // [-----------------<| BD FIREBASE |>-----------------] //

        FirebaseDatabase.getInstance().setPersistenceEnabled(true) //Cache
        FirebaseBD = FirebaseDatabase.getInstance().reference //Se obtiene la referencia
        BD_vinos = BD()
        FirebaseBD.orderByKey().addValueEventListener(taskListener)
        // [---------------------------------------------------] //



        //añadirVinos() //Ejemplo para añadir vinos





    }

    

    /**
     *  Pruebas para usar el metodo añadirVinos
     */

    fun añadirVinos(view: View){

        imagenUrl = BD_vinos.subirFoto(storage,imageView,"foto1")

        var obtenido = 0
        Thread{


            while (obtenido!=25){

                Log.i("VINUMLOG","IMAGEN URL BD: ${BD_vinos.urlImagen}")
                if(BD_vinos.urlImagen==null){

                    Thread.sleep(200)
                    obtenido++
                }else{


                    // Se declara un nuevo vino
                    val vino = Vino()

                    //Se añaden al menos sus elementos obligatorios
                    vino.nombre = "CUEVA DEL CHAMAN ROBLE2"
                    vino.anio = 2000
                    vino.bodega = "Prueba"
                    vino.uva = "Prueba"
                    vino.origen = "Prueba"
                    vino.descripcion = "Prueba"
                    vino.imagenUrl = BD_vinos.urlImagen

                    val resultado = BD_vinos.guardarVino(FirebaseBD,vino)
                    obtenido =25
                    if(resultado.second){

                        runOnUiThread {
                            //Si al añadir un vino este se añade satisfactoriamente
                            Toast.makeText(this@MainActivity,resultado.first,Toast.LENGTH_LONG).show()

                        }


                    }else{

                        runOnUiThread {
                            //Si al añadir un vino este se añade satisfactoriamente
                            Toast.makeText(this@MainActivity,resultado.first,Toast.LENGTH_LONG).show()

                        }
                    }

                }


            }
        }.start()




    }
}
