package com.example.e_fi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ActualizacionActivity : AppCompatActivity() {

    private var nuevaImagenUri: Uri? = null
    private var nuevoArchivoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)

        val nuevoNombreEditText: EditText = findViewById(R.id.nuevoNombreEditText)
        val nuevaFechaEditText: EditText = findViewById(R.id.nuevaFechaEditText)
        val subirNuevaImagenButton: Button = findViewById(R.id.subirNuevaImagenButton)
        val subirNuevoArchivoButton: Button = findViewById(R.id.subirNuevoArchivoButton)
        val actualizarButton: Button = findViewById(R.id.actualizarButton)

        subirNuevaImagenButton.setOnClickListener {
            // Intent para abrir la galería de imágenes
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        subirNuevoArchivoButton.setOnClickListener {
            // Intent para abrir el selector de archivos
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, REQUEST_TEXT_FILE_PICK)
        }

        actualizarButton.setOnClickListener {
            val nuevoNombre = nuevoNombreEditText.text.toString()
            val nuevaFecha = nuevaFechaEditText.text.toString()

            if (nuevaImagenUri != null && nuevoArchivoUri != null &&
                nuevoNombre.isNotEmpty() && nuevaFecha.isNotEmpty()
            ) {

                // Obtener el objeto Parse que deseas actualizar (puedes pasar esto como extra desde la actividad anterior)
                val objectId = "ID_DEL_OBJETO_A_ACTUALIZAR" // Reemplaza con el ID correcto
                val objetoAActualizar =
                    ParseObject.createWithoutData("NombreDeTuClaseParse", objectId)

                // Actualizar campos
                objetoAActualizar.put("nombre", nuevoNombre)
                objetoAActualizar.put("fecha", nuevaFecha)

                // Convertir la nueva imagen en bytes y almacenarla como ParseFile
                val nuevaImagenBytes = convertImageToBytes(nuevaImagenUri!!)
                val nuevaImagenFile = ParseFile("nueva_imagen.jpg", nuevaImagenBytes)
                objetoAActualizar.put("imagen", nuevaImagenFile)

                // Convertir el nuevo archivo de texto en bytes y almacenarlo como ParseFile
                val nuevoArchivoBytes = convertTextFileToBytes(nuevoArchivoUri!!)
                val nuevoArchivoFile = ParseFile("nuevo_archivo.txt", nuevoArchivoBytes)
                objetoAActualizar.put("archivoTexto", nuevoArchivoFile)

                // Guardar los cambios
                objetoAActualizar.saveInBackground { e ->
                    if (e == null) {
                        // Éxito al guardar en Parse
                        Toast.makeText(
                            this@ActualizacionActivity,
                            "Actualización exitosa",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Error al guardar en Parse
                        Toast.makeText(
                            this@ActualizacionActivity,
                            "Error al actualizar: $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // Al menos un campo está vacío o la imagen o el archivo de texto no se cargaron
                Toast.makeText(
                    this@ActualizacionActivity,
                    "Por favor, completa todos los campos y carga una nueva imagen y un nuevo archivo de texto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    // Obtener la URI de la imagen seleccionada
                    val selectedImage = data.data
                    nuevaImagenUri = selectedImage

                }

                REQUEST_TEXT_FILE_PICK -> {
                    // Obtener la URI del archivo de texto seleccionado
                    val selectedTextFile = data.data
                    nuevoArchivoUri = selectedTextFile
                }
            }
        }
    }

    private fun convertImageToBytes(imageUri: Uri): ByteArray {
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        val buffer = ByteArrayOutputStream()

        inputStream?.use { input ->
            val tempBuffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(tempBuffer).also { bytesRead = it } != -1) {
                buffer.write(tempBuffer, 0, bytesRead)
            }
        }

        return buffer.toByteArray()
    }

    private fun convertTextFileToBytes(textFileUri: Uri): ByteArray {
        val inputStream: InputStream? = contentResolver.openInputStream(textFileUri)
        val buffer = ByteArrayOutputStream()

        inputStream?.use { input ->
            val tempBuffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(tempBuffer).also { bytesRead = it } != -1) {
                buffer.write(tempBuffer, 0, bytesRead)
            }
        }

        return buffer.toByteArray()
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_TEXT_FILE_PICK = 2
    }
}