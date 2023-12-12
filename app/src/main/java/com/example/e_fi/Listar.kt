package com.example.e_fi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Date

class Listar:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView

        //Lista de objetos
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

    fun deleteFirma(id:String){
        val firmaEliminar:ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Firma_Electronica")
        firmaEliminar.whereEqualTo("objectId", id)
        try{
            val firma = firmaEliminar.first
            firma?.deleteInBackground(){ e ->
                if(e==null){
                    Toast.makeText(this@Listar, "Se ha eliminado la firma electronica",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@Listar, "Error al eliminar la firma electronica",
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

