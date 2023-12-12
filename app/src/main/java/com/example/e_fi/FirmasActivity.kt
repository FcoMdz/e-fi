package com.example.e_fi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Date

class FirmasActivity: AppCompatActivity(), OnDataUpdateListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firmas_views)
        val salir :ImageButton = findViewById(R.id.regresar)
        val agregar :ImageButton = findViewById(R.id.agregar)
        salir.setOnClickListener {
            finish()
        }
        agregar.setOnClickListener {
            val agregarView = Intent(this, RegistroActivity::class.java)
            startActivityForResult(agregarView,1)
        }

       this.updateFirmas()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            this.updateFirmas()
        }
        if (requestCode == 123) {

            this.updateFirmas()
        }
    }

    fun updateFirmas(){
        // Obtén la lista de firmas electrónicas, por ejemplo, desde tu otra clase
        val listaFirmas = obtenerFirmas()

        // Configura el adaptador y úsalo para mostrar la lista en el ListView
        if(listaFirmas != null){
            val adapter = FirmasAdapter(this, listaFirmas as List<firmaelectronica>, this)
            val listView: ListView = findViewById(R.id.listView)
            listView.adapter = adapter
        }
    }

    fun obtenerFirmas(): List<firmaelectronica?> {
        val firmas:ParseQuery<ParseObject> = ParseQuery.getQuery("Firma_Electronica")
        firmas.orderByAscending("fecha_emision")
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
                        parsed.getParseFile("cer")!!,
                        parsed.getParseFile("key")!!,
                        parsed.getDate("fecha_emision")!!,
                        parsed.getString("contacto")!!
                    )
                )
            }
            return convertidas.toList()
        }catch (err: ParseException) {
            err.printStackTrace()
            return convertidas
        }
    }

    override fun onDataUpdate() {
        Log.d("Respuesta", "Entre")
        this.updateFirmas()
    }

    class firmaelectronica(
        val objectId:String,
        val cliente:String,
        val RFC:String,
        val contrasena:String,
        val cer:ParseFile,
        val key:ParseFile,
        val fecha_emision: Date,
        val contacto: String
    )

}

