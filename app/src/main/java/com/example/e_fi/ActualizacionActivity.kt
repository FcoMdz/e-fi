package com.example.e_fi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ActualizacionActivity : AppCompatActivity() {

    private var selectedKeyUri: Uri? = null
    private var selectedCerUri: Uri? = null
    private lateinit var subirKeyButton: Button
    private lateinit var subirCerButton: Button
    private lateinit var id:String
    private lateinit var cliente:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)

        val intent = intent
        if(intent != null){
            this.id = intent.getStringExtra("id").toString()
            this.cliente = intent.getStringExtra("cliente").toString()
        }

        val regresarButton: ImageButton = findViewById(R.id.regresar)
        val nuevoNombreEditText: EditText = findViewById(R.id.nuevoNombreEditText)
        val nuevaFechaEditText: Button = findViewById(R.id.fechaEditButton)
        this.subirKeyButton = findViewById(R.id.subirKeyButton)
        this.subirCerButton = findViewById(R.id.subirCerButton)
        val actualizarButton: Button = findViewById(R.id.actualizarButton)
        val titulo:TextView = findViewById(R.id.titulo)

        if(this.cliente != null){
            titulo.text = titulo.text.toString() + " " + this.cliente
        }

        regresarButton.setOnClickListener {
            finish()
        }

        var fechaSeleccionada: Date? = null;

        subirKeyButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "application/*"
            startActivityForResult(intent, REQUEST_KEY_PICK)
        }
        subirCerButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.type = "application/*"
            startActivityForResult(intent, REQUEST_CER_PICK)
        }
        nuevaFechaEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    var selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    fechaSeleccionada = selectedDate.time
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    nuevaFechaEditText.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Mostrar el DatePickerDialog
            datePickerDialog.show()
        }

        actualizarButton.setOnClickListener {
            val nuevoNombre = nuevoNombreEditText.text.toString()
            val nuevaFecha = nuevaFechaEditText.text.toString()

            if (selectedKeyUri != null && selectedCerUri != null &&
                nuevoNombre.isNotEmpty() && nuevaFecha.isNotEmpty() &&
                fechaSeleccionada != null && this.id != null
            ) {

                // Obtener el objeto Parse que deseas actualizar (puedes pasar esto como extra desde la actividad anterior)
                val objectId = "ID_DEL_OBJETO_A_ACTUALIZAR" // Reemplaza con el ID correcto
                val query = ParseQuery.getQuery<ParseObject>("Firma_Electronica")
                query.whereEqualTo("objectId", this.id)
                try{
                    val objetoAActualizar =
                        query.first
                    val certificado = convertTextFileToBytes(selectedCerUri!!)
                    val cert = ParseFile(subirCerButton.text.toString(), certificado)
                    val llave = convertTextFileToBytes(selectedKeyUri!!)
                    val key = ParseFile(subirKeyButton.text.toString(), llave)
                    objetoAActualizar?.apply {
                        put("nombre", nuevoNombre)
                        put("fecha_emision", fechaSeleccionada!!)
                        put("cer", cert)
                        put("key", key)
                    }
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
                }catch (e: ParseException){
                    e.printStackTrace()
                }
            } else {
                // Al menos un campo está vacío o la imagen o el archivo de texto no se cargaron
                Toast.makeText(this@ActualizacionActivity, "Completa todos los campos y carga la firma electrónica", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CER_PICK -> {
                    // Obtener la URI de la imagen seleccionada
                    val selectedData = data.data
                    selectedCerUri = selectedData
                    if(this.subirCerButton!=null && selectedData != null) this.subirCerButton.text = getFileName(selectedData)
                }
                REQUEST_KEY_PICK -> {
                    // Obtener la URI del archivo de texto seleccionado
                    val selectedData = data.data
                    selectedKeyUri = selectedData
                    if(this.subirKeyButton!=null && selectedData != null) this.subirKeyButton.text = getFileName(selectedData)
                }
                else -> {
                    if(this.subirKeyButton!=null && this.subirCerButton!=null){
                        this.subirKeyButton.text = "Subir archivo key"
                        this.subirCerButton.text = "Subir archivo cer"
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        val scheme = uri.scheme
        if (scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        } else if (scheme == "file") {
            result = uri.lastPathSegment
        }
        return result
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
        private const val REQUEST_CER_PICK = 1
        private const val REQUEST_KEY_PICK = 2
    }
}