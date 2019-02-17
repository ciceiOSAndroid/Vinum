package com.m600.alumnos.cice.vinum

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera_vinum.*

class CameraVinumActivity : AppCompatActivity() {

    /**
     * Estas variables genéricas corresponden con códigos de petición para permisos y
     * fotografía
     */
    private val PHOTO_INTENT = 102
    private val RECORD_REQUEST_CODE = 103

    /**
     * Este Array guarda las imágenes de la cámara antes de ser enviadas al segundo activity
     */
    private var imagenes = ArrayList<Bitmap>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_vinum)

        Log.i("TAG", "El dispositivo tiene cámara: ${comprobarCamara()}")
    }

    /**
     * @Method comprobarPermisos hace gala de su nombre con respecto al Manifest
     * @object permiso declara el contexto, representado en ContextCompat(por tratarse de una clase
     * que hereda de AppCompatActivity) y el método de test .checkSelfPermission y recibe el
     * contexto (this), y el permiso en el manifest(de ANDROID) de grabación de audio.
     * if statement plantea que si el permiso es distinto de garantizado, podemos un Log.i y
     * presentamos un Toast, avisando al usuario que el permiso está siendo denegado.
     * @Method solicitarPermiso() configurado debajo -> se invoca para que vuelva a salir la
     * solicitud de permiso.
     * else statement plantea mensaje para reafirmar la concesión del permiso.
     */
    fun comprobarPermisos() {
        val permiso = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
        if (permiso != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permiso para usar la cámara denegado")
            Toast.makeText(this, "Permiso para cámara denegado",
                Toast.LENGTH_LONG).show()
            //Solicitar permiso
            solicitarPermiso()
        } else {
            Toast.makeText(this, "Permiso para cámara permitido",
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     * El permiso debe solicitarse para cada uno y en el momento en el que lo vamos a solicitar
     * @Method requestPermissions->  comprueba si tenemos el permiso con el Activity compatible
     * @param "RECORD_REQUEST_CODE" equivale al código declarado como variable arriba a nivel de clase
     */
    private fun solicitarPermiso() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
            RECORD_REQUEST_CODE)
    }

    /**
     * Método onRequestPermissionsResult -> configura el sistema de recepción del permiso
     * @Method shouldShowRequestPermissionRationale()->
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                //Si no nos ha concedido el permiso
                Toast.makeText(this, "Permiso NO concedido", Toast.LENGTH_LONG).show()
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    //Creamos nuestro alert dialogo propio
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("IMPORTANTE")
                    dialog.setMessage("Permisos en VINUM son necesarios")
                    /**
                     * Configuramos botones del AlertDialog
                     */
                    dialog.setPositiveButton("OK") { dialog, which ->
                        Toast.makeText(this, "¡Gracias!",
                            Toast.LENGTH_LONG).show()
                    }

                    dialog.setNegativeButton("RECHAZAR") { dialog, which ->
                        Toast.makeText(this, "Permiso rechazado",
                            Toast.LENGTH_LONG).show()
                        solicitarPermiso()
                    }

                    val alerta = dialog.create()
                    alerta.show()
                } else {
                    solicitarPermiso()
                }

            } else {
                Toast.makeText(this, "Permiso concedido",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * @Method comprobarCamara-> Devuelve un Boolean, accedemos al packageManager con un
     * @return, llamando al
     * @Method hasSystemFeature es un test que devuelve true si el servicio solicitado existe...
     * @Hardware .Feature_Camera_Any comprueba si el sistema tiene cualquier cámara.
     */
    fun comprobarCamara(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /**
     * @Method startPhotoCamera-> contiene el intent que lanza la actividad de la cámara.
     * @Class MediaStore -> inicializa el Intent con la acción que queremos capturar
     * @Action .ACTION_IMAGE_CAPTURE-> se refiere a la cámara de fotos.
     * @Method startActivityForResult-> arranca el intent con el resultado del proceso realizado
     * @Identificator PHOTO_INTENT-> corresponde al código contenido en val PHOTO_INTENT creada
     * arriba a nivel de clase.
     */
    fun startPhotoCamera(view: View) {
        if (checkBoxDifusionPhotoVinum.isChecked) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, PHOTO_INTENT)
        } else {
            Toast.makeText(this, "Por favor autoriza la difusión de tu Foto Vinum",
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     *@Method checkPermisoAuth enlaza una petición de concesión de difusión de la foto tomada por el
     * usuario con los permisos necesarios para accionar la cámara
     */
    fun checkPermisoAuth(view: View) {
        if (checkBoxDifusionPhotoVinum.isChecked)
            comprobarPermisos()
    }

    /**
     * @Method onActivityResult-> Es super importante: plantea un statement que consulta si el
     * @Param requestCode equivale al código de petición, si eso es así, sacamos un Toast con
     * la data de dónde está la foto (la ruta| path)
     * @object imageBitmap -> asigna a la variable creada a nivel de clase, la ruta del uri de la
     * foto -> data?.data
     * El siguiente if statement cuestiona el éxito del código requerido y sus actividad asociada,
     * con un Toast que informa la ruta de la foto recién tomada y finalmente se añade la
     * imagen captada en formato Bitmap antes de ser enviada al siguiente activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == PHOTO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {

                val imageBitmap = data?.extras?.get("data") as Bitmap
                Toast.makeText(this, " Tu foto está en ${data?.extras?.get("data")}",
                    Toast.LENGTH_LONG).show()

                //Proyectamos la imagen en su ImageView de vista previa
                imageViewPhotoVinumPpal.setImageBitmap(imageBitmap)
                //Incluimos la foto en su array
                imagenes.add(imageBitmap)
            }

        }
    }

    /**
     * @Method verPhotoButton-> configura el intent que envía las imágenes de la cámara de fotos
     * al activity "conectado" -> AgregarVinoActivity
     * @if_Statement supedita la acción del botón al contenido del array entre el Intent y un
     * Toast
     */
    fun AgregarPhotoButton(view: View) {
        if(imagenes.size>0) {
            //val intent = Intent(this, AgregarVinosActivity::class.java)
            intent.putExtra("codigo", "photo")
            intent.putParcelableArrayListExtra("imageUri", imagenes)
            startActivity(intent)
        }else {
            Toast.makeText(this,
                " Por favor toma una foto", Toast.LENGTH_LONG).show()
        }

    }
}
