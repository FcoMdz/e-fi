package com.example.e_fi

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import java.text.SimpleDateFormat
import java.util.Locale

class FirmasAdapter(
    context: Context,
    private val firmaList: List<FirmasActivity.firmaelectronica>,
    private val onDataUpdateListener: OnDataUpdateListener
) : ArrayAdapter<FirmasActivity.firmaelectronica>(context, R.layout.item_firma, firmaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_firma, parent, false)
        val firma = getItem(position)
        val parent:ViewGroup = view.findViewById(R.id.parent)
        val clienteTextView: TextView = view.findViewById(R.id.clienteTextView)
        val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val rfcTextView:TextView = view.findViewById(R.id.RFCTextView)
        val contrasenaTextView:TextView = view.findViewById(R.id.ContraTextView)
        val editarButton: ImageButton = view.findViewById(R.id.editarButton)
        val contactoButton: ImageButton = view.findViewById(R.id.contactoButton)
        val deleteButton: ImageButton = view.findViewById(R.id.eliminarButton)
        val descargarButton: ImageButton = view.findViewById(R.id.descargarButton)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(firma?.fecha_emision?.time)
        clienteTextView.text = "${firma?.cliente}"
        fechaTextView.text = "$formattedDate"
        rfcTextView.text = "${firma?.RFC}"
        contrasenaTextView.text = "${firma?.contrasena}"

        contactoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${firma?.contacto}")
            startActivity(context, intent, null)
        }

        editarButton.setOnClickListener {
            val intent = Intent(context, ActualizacionActivity::class.java)
            intent.putExtra("id", "${firma?.objectId}")
            intent.putExtra("cliente", "${firma?.cliente}")
            startActivityForResult(context as Activity, intent, 123, null)
        }

        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Seguro que desea eliminar la firma de ${firma?.cliente}?")
            builder.setPositiveButton("Sí") { _, _ ->
                onDataUpdateListener.onDataUpdate()
            }

            builder.setNegativeButton("No") { _, _ ->
                onDataUpdateListener.onDataUpdate()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        return view
    }

}

interface OnDataUpdateListener {
    fun onDataUpdate()
}