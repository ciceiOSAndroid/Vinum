package com.m600.alumnos.cice.vinum


class Vino {



    companion object Factory{

        fun create():Vino = Vino()

    }

    var ID: String ?= null
    var nombre: String ?= null
    var anio: Long ?= null
    var uva: String ?= null
    var grados: String ?= null
    var origen: String ?= null
    var bodega: String ?= null
    var descripcion: String ?= null
    var puntuaciones: HashMap<String,Int> ?= null
    //Almacena la URL de la cual se obtendrá la imagen
    var imagen: String ?= null



}