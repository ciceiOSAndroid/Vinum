package com.m600.alumnos.cice.vinum

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Vino constructor(val name: String,
                           val features: ArrayList<String> = ArrayList<String>()
): Parcelable {

    constructor(source: Parcel) : this(
        source.readString(),
        source.createStringArrayList()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeStringList(features)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Vino>{
        override fun createFromParcel(source: Parcel?): Vino {
            return Vino(source!!)
        }

        override fun newArray(size: Int): Array<Vino?> {
            return arrayOfNulls(size)
        }

    }
    //tarea lista vacia por eso declara, tiene un valor por defecto, si lo paso utiliza el pasado
    //guarda en shared preferences

}