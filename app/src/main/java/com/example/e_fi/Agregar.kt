package com.example.e_fi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseFile
import com.parse.ParseObject
import java.io.File
import java.util.Date

class Agregar: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView

        //Datos necesarios
        //cliente
        //RFC
        //contrasena
        //cer
        //key
        //fecha
        //contacto

    }

    fun agregar(cliente:String, RFC:String, contrasena:String, cer:ByteArray, key:ByteArray, fecha:Date, contacto:String){
        var firma = ParseObject("Firma_Electronica")
        firma.put("cliente", cliente)
        firma.put("RFC", RFC)
        firma.put("contrasena", contrasena)
        firma.put("cer", ParseFile("$RFC.cer", cer))
        firma.put("key", ParseFile( "$RFC.key", key))
        firma.put("fecha_emision", fecha)
        firma.put("contacto", contacto)

        firma.saveInBackground {e ->
            if(e == null){
                //Exito al subir los datos
                Toast.makeText(this@Agregar, "Se ha agregado la firma electrónica",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@Agregar, "Ha fallado el agregar la firma",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}