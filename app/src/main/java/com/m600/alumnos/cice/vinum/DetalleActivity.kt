package com.m600.alumnos.cice.vinum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_detalle.*

class DetalleActivity : AppCompatActivity() {
    private var nombre = ""
    private var descripcion = ""
    private var precio = ""
    private var foto = 0
    private var ano = ""
    private var uva = ""
    private var origen = ""
    private var bodega = ""
    private var grados = ""
    private var punt1 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        fade() //funcion para la animacion de la app

        //recogida de extras
        nombre = intent.getStringExtra("nombre")
        descripcion = intent.getStringExtra("descripcion")
        precio = intent.getStringExtra("precio")
        foto = intent.getIntExtra("foto", 0)
        ano = intent.getStringExtra("año")
        uva = intent.getStringExtra("uva")
        origen = intent.getStringExtra("origen")
        bodega = intent.getStringExtra("bodega")
        grados = intent.getStringExtra("grados")
        punt1 = intent.getStringExtra("punt1")

        //asignacion de valores
        fotoVino.setImageResource(foto)
        nombreTV.text = nombre
        precioTV.text = "$precio€"
        anoTV.text = "Año: $ano"
        uvaTV.text = "Uva $uva"
        origenTV.text = "Origen $origen"
        bodegaTV.text = "Bodega: $bodega"
        gradosTV.text = "Graduación: $grados"
        puntuacionTV.text = "Puntuación: $punt1"
        descrTV.text = descripcion

        animacion()

        botonCesta.setOnClickListener {
            ListDataManager(this).saveList(Vino)
        }


    }

    fun fade(){
        var fade = Fade()
        var decor = window.decorView
        fade.excludeTarget(decor.findViewById<View>(R.id.action_bar_container), true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
    }

    fun animacion() {
        val animacion1 = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        val animacion2 = AnimationUtils.loadAnimation(this, R.anim.slide_left)
        constraintDesc.startAnimation(animacion1)
        constraintPunt.startAnimation(animacion2)

    }


}
