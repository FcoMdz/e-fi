package com.example.e_fi

import android.content.Context
import android.widget.Toast
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Date

class Backend {
    fun agregar(context:Context, cliente:String, RFC:String, contrasena:String, cer:ByteArray, key:ByteArray, fecha:Date, contacto:String){
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
                Toast.makeText(context, "Se ha agregado la firma electrónica",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Ha fallado el agregar la firma",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateFirma(context:Context, id:String, cer:ByteArray, key:ByteArray, contacto:String, fecha:Date){
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
                    Toast.makeText(context, "Se ha actualizado la firma con exito",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Ha fallado la actualización de la firma",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }
    }

    fun obtenerFirmas(): ArrayList<firmaelectronica?> {
        val firmas:ParseQuery<ParseObject> = ParseQuery.getQuery("Firma_Electronica")
        val convertidas = ArrayList<firmaelectronica?>(0)
        try{
            val objects = firmas.find()
            for(parsed in objects){
                convertidas.add(
                    firmaelectronica(
                        parsed.objectId,
                        parsed.getString("cliente")!!,
                        parsed.getString("RFC")!!,
                        parsed.getString("contrasena")!!,
                        parsed.getBytes("cer")!!,
                        parsed.getBytes("key")!!,
                        parsed.getDate("fecha_emision")!!,
                        parsed.getString("contacto")!!
                    )
                )
            }
        }catch (err: ParseException){
            err.printStackTrace()
        }
        return convertidas
    }

    fun deleteFirma(context: Context, id:String){
        val firmaEliminar:ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Firma_Electronica")
        firmaEliminar.whereEqualTo("objectId", id)
        try{
            val firma = firmaEliminar.first
            firma?.deleteInBackground(){ e ->
                if(e==null){
                    Toast.makeText(context, "Se ha eliminado la firma electronica",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Error al eliminar la firma electronica",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }

    }
    class firmaelectronica(
        val objectId:String,
        val cliente:String,
        val RFC:String,
        val contrasena:String,
        val cer:ByteArray,
        val key:ByteArray,
        val fecha_emision:Date,
        val contacto: String
    ){
    }
}

