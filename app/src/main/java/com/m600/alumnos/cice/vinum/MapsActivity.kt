package com.m600.alumnos.cice.vinum

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var baseDatos: DatabaseReference

    //Listener que ejecuta una funccion pasandole la base de datos cuando hay algun cambio en la base de datos
    var taskListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            //..
        }
        override fun onDataChange(p0: DataSnapshot) {
            buscarBodegas(p0)
        }
    }

    fun buscarBodegas(dataSnapshot: DataSnapshot) {
        val tareas = dataSnapshot.children.iterator()
        var setBodegas: MutableSet<String> = mutableSetOf()
        if (tareas.hasNext()) {
            val listaIndex = tareas.next()
            val itemsIterator = listaIndex.children.iterator()

            while (itemsIterator.hasNext()) {
                //Obtenemos la tarea y su info
                val tareActual = itemsIterator.next()
                val map = tareActual.getValue() as HashMap<String, Any>

                //Creo un set con las bodegas para que no se repita el marcador si hay alguna bodega repetida
                setBodegas.add(map["bodega"] as String)
            }
        }
        //Llamo a la clase asincrona para buscar las coordenadas de cada bodega y crear sus marcadores en el mapa
        for (address in setBodegas) {
            var getPlaces = GetPlaces(this, address)
            getPlaces.execute()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        baseDatos = FirebaseDatabase.getInstance().reference
        baseDatos.orderByKey().addValueEventListener(taskListener)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Especificar las características del mapa
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.isIndoorEnabled = true

        //Activar funcionalidades especiales
        val mapSettings = mMap.uiSettings
        mapSettings.isZoomControlsEnabled = true
        mapSettings.isRotateGesturesEnabled = true
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.407, -3.696), 5f))

    }

    private fun crearMarcador(address: Address) {
        // Crear un marcador
        val posicion = LatLng(address.latitude, address.longitude)

        val markerOptions = MarkerOptions()
        markerOptions.title(address.featureName)
        markerOptions.snippet(address.locality)
        markerOptions.position(posicion)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icovino))
        mMap.addMarker(markerOptions)

        //Cámara
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 6f))
    }


    internal inner class GetPlaces(var context: Context, var address: String) : AsyncTask<Void, Void, Void>() {

        var geocodeAnswer: List<Address>? = null

        override fun doInBackground(vararg params: Void?): Void? {

            try {
                geocodeAnswer = Geocoder(context).getFromLocationName(address, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (geocodeAnswer != null && geocodeAnswer!!.isNotEmpty()) {
                geocodeAnswer!![0].featureName = address
                crearMarcador(geocodeAnswer!![0])
            } else {
                Log.i("TAG", "No se encotro localizacion para: $address")
            }
        }
    }
}
