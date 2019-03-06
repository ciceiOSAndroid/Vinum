package com.m600.alumnos.cice.vinum

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_agregar_vino.*
import java.util.*

class AgregarVinosActivity : AppCompatActivity() {

    private var bitmapImage: Bitmap? = null
    private lateinit var FirebaseBD: DatabaseReference // BD FIREBASE
    lateinit var BD_vinos: BD
    val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vino)

        //Conecto la seekbar del layout al TextView
        conectarSeekBar()

        //Agrego la foto que me envia la activity Camara
        agregarFoto()

        //Configuro el spinner
        configurarSpinner()

        //Crear una instanca para el Firebase DataBase
        FirebaseBD = FirebaseDatabase.getInstance().reference
        BD_vinos = BD()

    }


    /**
     * Enlazo el movimieto de la seekbar con el textview
     */
    private fun conectarSeekBar() {

        graduacion_SB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                resultado_TV.text = "${i}º"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    /**
     * Al clicar el boton agregar añade el vino  a la database
     */
    fun agregarVino(view: View) {

        Log.i("TAG", "${fecha_ET.selectedItem.toString().toLong()}")


        if (vino_IV != null) {

            BD_vinos.subirFoto(
                storage,
                vino_IV,
                "${nombre_ET.text}${fecha_ET.selectedItemId}${ratingBar.rating}"
            )

            var intento = 0

            Thread {
                /**[2]**/ // hará 30 comprobaciones cada 200m hasta que se haya subido la imagen
                while (intento != 30) {

                    Thread.sleep(200)

                    if (BD_vinos.urlImagen == null) {

                        intento++

                        if (intento == 30) {

                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    "No se ha podido subir la foto intentalo de nuevo",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } else {

                        val vino = Vino2()

                        vino.nombre = nombre_ET.text.toString()
                        vino.anio = fecha_ET.selectedItem.toString().toLong()
                        vino.bodega = bodega_ET.text.toString()
                        vino.descripcion = descripcion_ET.text.toString()
                        vino.grados = resultado_TV.text.toString()
                        vino.uva = uva_ET.text.toString()
                        vino.origen = origen_ET.text.toString()
                        vino.puntuacion = ratingBar.rating.toLong() * 2
                        vino.imagen = bitmapImage
                        vino.imagenUrl = BD_vinos.urlImagen


                        val resultado = BD_vinos.guardarVino(FirebaseBD, vino)

                        intento = 30

                        if (resultado.second) {

                            runOnUiThread {
                                Toast.makeText(
                                    this, resultado.first, Toast.LENGTH_LONG
                                ).show()

                            }

                        } else {

                            runOnUiThread {
                                Toast.makeText(
                                    this, resultado.first, Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    }


                }
            }.start()
        }

    }


    /**
     * Agrega la imagen recibida como extra al layout
     */
    fun agregarFoto() {

        try {
            var foto = intent.getParcelableArrayListExtra<Parcelable>("imageUri")

            vino_IV.setImageBitmap(foto[0] as Bitmap)
            bitmapImage = foto[0] as Bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Configura las diferencetes opciones del spinner
     */
    fun configurarSpinner() {

        val myStrings = mutableListOf<Long>()

        var i = 0
        var fecha: Long = Calendar.getInstance().get(Calendar.YEAR).toLong()
        while (i < 218) {
            myStrings.add(fecha)
            i += 1
            fecha -= 1
        }


        fecha_ET.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, myStrings)


        fecha_ET.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
    }

    /**
     * Finalizo el activity, vuelvo a la camra por si se quiere repetir la foto
     */
    fun cancelarAction(view: View) {

        finish()
    }
}

