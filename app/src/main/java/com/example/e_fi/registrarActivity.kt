package com.example.e_fi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class RegistroActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var selectedTextFileUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_layout)

        val nombreEditText: EditText = findViewById(R.id.nombreEditText)
        val contrasenaEditText: EditText = findViewById(R.id.contrasenaEditText)
        val correoEditText: EditText = findViewById(R.id.correoEditText)
        val rfcEditText: EditText = findViewById(R.id.rfcEditText)
        val fechaEditText: EditText = findViewById(R.id.fechaEditText)
        val subirImagenButton: Button = findViewById(R.id.subirImagenButton)
        val subirArchivoButton: Button = findViewById(R.id.subirArchivoButton)
        val registrarButton: Button = findViewById(R.id.registrarButton)

        subirImagenButton.setOnClickListener {
            // Intent para abrir la galería de imágenes
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
        subirArchivoButton.setOnClickListener {
            // Intent para abrir el selector de archivos
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/*"
            startActivityForResult(intent, REQUEST_TEXT_FILE_PICK)
        }
        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()
            val rfc = rfcEditText.text.toString()
            val fecha = fechaEditText.text.toString()
            val correo = correoEditText.text.toString()
            // Validar que la imagen se haya cargado antes de proceder con el registro
            if (selectedImageUri != null && selectedTextFileUri != null &&
                nombre.isNotEmpty() && contrasena.isNotEmpty() && rfc.isNotEmpty()
                && fecha.isNotEmpty() && correo.isNotEmpty()) {

                val efirma = ParseObject("Firma_Electronica")
                efirma.put("contrasena", contrasena)
                efirma.put("contacto", correo)
                efirma.put("cliente", nombre)
                efirma.put("fecha_emision", fecha)
                val imageBytes = convertImageToBytes(selectedImageUri!!)
                val imageFile = ParseFile("imagen.jpg", imageBytes)
                efirma.put("imagen", imageFile)
                val textFileBytes = convertTextFileToBytes(selectedTextFileUri!!)
                val textFile = ParseFile("archivo.txt", textFileBytes)
                efirma.put("archivoTexto", textFile)

                // Muestra un mensaje de registro exitoso
                Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
            } else {
                // Al menos un campo está vacío o la imagen no se cargó
                Toast.makeText(this@RegistroActivity, "Por favor, completa todos los campos y carga una imagen", Toast.LENGTH_SHORT).show()
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
                    selectedImageUri = selectedImage

                }
                REQUEST_TEXT_FILE_PICK -> {
                    // Obtener la URI del archivo de texto seleccionado
                    val selectedTextFile = data.data
                    selectedTextFileUri = selectedTextFile
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
