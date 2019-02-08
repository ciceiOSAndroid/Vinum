package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {



    // [-----------------<| BD FIREBASE |>-----------------] //

    //Se declara la referencia a la base de datos
    lateinit var FirebaseBD: DatabaseReference // BD FIREBASE
    lateinit var BD_vinos: BD // Gestor de la BD

    var taskListener: ValueEventListener = object : ValueEventListener {

        override fun onCancelled(p0: DatabaseError) {

            //Nothing
        }

        override fun onDataChange(p0: DataSnapshot) {

            // Devuelve todos los vinos como ArrayList<Vino>?
            BD_vinos.cargarVinos(p0)

            //taskList!!.add(BD_vinos)
            //adapter.notifyDataSetChanged()
        }
    }
    // [---------------------------------------------------] //



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) //BD FIREBASE
        setContentView(R.layout.activity_main)


        // [-----------------<| BD FIREBASE |>-----------------] //

        FirebaseDatabase.getInstance().setPersistenceEnabled(true) //Cache
        FirebaseBD = FirebaseDatabase.getInstance().reference //Se obtiene la referencia



        FirebaseBD.setValue("Hello, World!")
// [---------------------------------------------------] //


}
}
