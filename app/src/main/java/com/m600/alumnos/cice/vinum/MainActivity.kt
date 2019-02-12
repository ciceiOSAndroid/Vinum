package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import android.os.StrictMode



class MainActivity : AppCompatActivity() {



    // [-----------------<| BD FIREBASE |>-----------------] //

    //Se declara la referencia a la base de datos
    private lateinit var FirebaseBD: DatabaseReference // BD FIREBASE
    lateinit var BD_vinos: BD // Gestor de la BD

    var taskListener: ValueEventListener = object : ValueEventListener {

        override fun onCancelled(p0: DatabaseError) {

            //Nothing
        }


        // [-----------------<| PRUEBA MOSTRAR VINOS |>-----------------] /
        override fun onDataChange(p0: DataSnapshot) {

            // Devuelve todos los vinos como ArrayList<Vino>?
            val vinos = BD_vinos.cargarVinos(p0)

            //Se recorren los resultados y se muestran
            for (item in vinos!! ){

                Log.i("VINOS","Nombre: ${item.nombre}\n" +
                        "anio: ${item.anio} \n" +
                        "bodega: ${item.bodega}\n" +
                        "uva: ${item.uva}\n" +
                        "origen: ${item.origen}\n" +
                        "descripcion: ${item.descripcion}\n")

            }

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



        añadirVinos() //Ejemplo para añadir vinos


    }



    /**
     *  Pruebas para usar el metodo añadirVinos
     */

    fun añadirVinos(){

        // Se declara un nuevo vino
        val vino = Vino()

        //Se añaden al menos sus elementos obligatorios
        vino.nombre = "CUEVA DEL CHAMAN ROBLlE"
        vino.anio = 2000
        vino.bodega = "Prueba"
        vino.uva = "Lol"
        vino.origen = "España"
        vino.descripcion = "dfksjdfksdfjksdjkfknsdf"

        val resultado = BD_vinos.guardarVino(FirebaseBD,vino)

        if(resultado.second){

            //Si al añadir un vino este se añade satisfactoriamente
            Toast.makeText(this@MainActivity,resultado.first,Toast.LENGTH_LONG).show()


        }else{

            //Si se ha ocurrido un error o los datos son incorrectos
            Toast.makeText(this@MainActivity,resultado.first,Toast.LENGTH_LONG).show()

        }



    }
}
