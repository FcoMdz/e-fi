package com.example.e_fi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseObject
import com.parse.ParseQuery

class FirmasActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firmas_views)

        // Obtén la lista de firmas electrónicas, por ejemplo, desde tu otra clase
        val listaFirmas = Backend.obtenerFirmas()

        // Configura el adaptador y úsalo para mostrar la lista en el ListView
        val adapter = FirmasAdapter(this, listaFirmas)
        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = adapter
    }

