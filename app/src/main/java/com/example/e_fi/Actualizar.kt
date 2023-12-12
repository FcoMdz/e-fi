package com.example.e_fi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Date

class Actualizar: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun updateFirma(id:String, cer:ByteArray, key:ByteArray, contacto:String, fecha:Date){
        val query = ParseQuery.getQuery<ParseObject>("Firma_Electronica")
        query.whereEqualTo("objectId", id)
        try{
            val objectToUpdate = query.first
            objectToUpdate?.apply {
                put("cer", ParseFile("${objectToUpdate.getString("RFC")}.cer", cer))
                put("key", ParseFile("${objectToUpdate.getString("RFC")}.key", key))
                put("fecha_emision", fecha)
                put("contacto", contacto)
            }
            objectToUpdate?.saveInBackground(){e ->
                if(e==null){
                    Toast.makeText(this@Actualizar, "Se ha actualizado la firma con exito",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@Actualizar, "Ha fallado la actualización de la firma",
                    Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }
    }
}