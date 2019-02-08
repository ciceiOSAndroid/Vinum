package com.m600.alumnos.cice.vinum

class Vino {

    companion object Factory{

        fun create():Vino = Vino()

    }

    var objectId: String ?= null
    var taskDesc: String ?= null
    var done: Boolean ?= false


}