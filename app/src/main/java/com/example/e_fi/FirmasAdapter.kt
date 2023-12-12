package com.example.e_fi

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

class FirmasAdapter(
    context: Context,
    private val firmaList: List<Backend.firmaelectronica>
) : ArrayAdapter<Backend.firmaelectronica>(context, R.layout.item_firma, firmaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_firma, parent, false)

        val firma = getItem(position)

        val clienteTextView: TextView = view.findViewById(R.id.clienteTextView)
        val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val editarButton: Button = view.findViewById(R.id.editarButton)

        clienteTextView.text = "Cliente: ${firma?.cliente}"
        fechaTextView.text = "Fecha: ${firma?.fecha_emision}"

        editarButton.setOnClickListener {

        }

        return view
    }
}
