package com.m600.alumnos.cice.vinum

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*
import android.R
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Spinner
import android.widget.ArrayAdapter
import java.io.File
import java.nio.file.Files.isDirectory
import java.nio.file.Files.exists





class ListDataManager(val context: Context) {
    //Guardar en Shared Preferences
        //PreferenceManager

    fun saveList(list: Vino){ //pide context
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
            sharedPreferences.putStringSet(list.name, list.features.toHashSet()) //SACAR EL HASH, PARA METER EN EL SHARED PREFERENCES
            sharedPreferences.apply()
    }



    //Recuperar en Shared Preferences
    fun getLists(): ArrayList<Vino> { //pide context
        Log.d("ResumenPedidos", "ENTRA")

        val vinos = ArrayList<Vino>()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val sharedPreferencesContent = sharedPreferences.all


        for(vino in sharedPreferencesContent) {
            val itemHashSet = vino.value as HashSet<String>
            val lista = Vino(vino.key, ArrayList(itemHashSet))
            vinos.add(lista)
        }
        if (vinos == null) Log.i("ResumenPedidos", "HOLAAA")

        //Log.d("ResumenPedidos", vinos[0].name)
        return vinos
    }


    fun deleteVino(name: String){
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().remove(name).commit()
    }


}