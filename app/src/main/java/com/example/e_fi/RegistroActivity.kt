package com.example.e_fi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    private var selectedKeyUri: Uri? = null
    private var selectedCerUri: Uri? = null
    private lateinit var subirKeyButton: Button
    private lateinit var subirCerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_layout)

        val nombreEditText: EditText = findViewById(R.id.nombreEditText)
        val contrasenaEditText: EditText = findViewById(R.id.contrasenaEditText)
        val correoEditText: EditText = findViewById(R.id.correoEditText)
        val rfcEditText: EditText = findViewById(R.id.rfcEditText)
        val fechaEditText: Button = findViewById(R.id.fechaEditButton)
        this.subirKeyButton = findViewById(R.id.subirKeyButton)
        this.subirCerButton = findViewById(R.id.subirCerButton)
        val registrarButton: Button = findViewById(R.id.registrarButton)
        val regresarButton: ImageButton = findViewById(R.id.regresar)

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
        fechaEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    var selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    fechaSeleccionada = selectedDate.time
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    fechaEditText.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Mostrar el DatePickerDialog
            datePickerDialog.show()
        }

        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()
            val rfc = rfcEditText.text.toString()
            val correo = correoEditText.text.toString()
            // Validar que la imagen se haya cargado antes de proceder con el registro
            if (selectedKeyUri != null && selectedCerUri != null &&
                nombre.isNotEmpty() && contrasena.isNotEmpty() && rfc.isNotEmpty()
                && fechaSeleccionada != null && correo.isNotEmpty()) {
                val nfecha = Date()
                val efirma = ParseObject("Firma_Electronica")
                efirma.put("contrasena", contrasena)
                efirma.put("contacto", correo)
                efirma.put("cliente", nombre)
                efirma.put("fecha_emision", fechaSeleccionada!!)
                efirma.put("RFC", rfc)
                val certificado = convertTextFileToBytes(selectedCerUri!!)
                val cert = ParseFile(subirCerButton.text.toString(), certificado)
                efirma.put("cer", cert)
                val llave = convertTextFileToBytes(selectedKeyUri!!)
                val key = ParseFile(subirKeyButton.text.toString(), llave)
                efirma.put("key", key)

                efirma.saveInBackground {e ->
                    if(e == null){
                        //Exito al subir los datos
                        Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        nombreEditText.text = Editable.Factory.getInstance().newEditable("")
                        contrasenaEditText.text = Editable.Factory.getInstance().newEditable("")
                        correoEditText.text = Editable.Factory.getInstance().newEditable("")
                        rfcEditText.text = Editable.Factory.getInstance().newEditable("")
                        fechaEditText.text = "Seleccionar fecha de registro"
                        subirKeyButton.text = "Subir archivo key"
                        subirCerButton.text = "Subir archivo cer"
                        selectedKeyUri = null
                        selectedCerUri = null
                    }else{
                        Toast.makeText(this@RegistroActivity, "Ha fallado el registro",
                            Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                // Al menos un campo está vacío o la imagen no se cargó
                Toast.makeText(this@RegistroActivity, "Completa todos los campos y carga la firma electrónica", Toast.LENGTH_SHORT).show()
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
            val tempBuffer = ByteArray(4096)
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
