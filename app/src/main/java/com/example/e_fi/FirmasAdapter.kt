package com.example.e_fi

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.getColor
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.Locale

class FirmasAdapter(
    context: Context,
    private val firmaList: List<FirmasActivity.firmaelectronica>,
    private val onDataUpdateListener: OnDataUpdateListener
) : ArrayAdapter<FirmasActivity.firmaelectronica>(context, R.layout.item_firma, firmaList) {

    @RequiresApi(Build.VERSION_CODES.O)
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

        //Logica para colocar el fondo de distintos colores según los años
        val calendarioActual = LocalDate.now()
        val calendarioFecha =
            firma?.fecha_emision?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate()

        val diferenciaAnos = Period.between(calendarioFecha, calendarioActual)
        when{
            diferenciaAnos.years + (diferenciaAnos.months / 12) + (diferenciaAnos.days / 365.25) > 3 -> {
                parent.background = ColorDrawable(getColor(context, R.color.rojo))
            }
            diferenciaAnos.years + (diferenciaAnos.months / 12) + (diferenciaAnos.days / 365.25) > 1 -> {
                parent.background = ColorDrawable(getColor(context, R.color.amarillo))
            }
            else -> {
                parent.background = ColorDrawable(getColor(context, R.color.verde))
            }
        }

        descargarButton.setOnClickListener {
            val request1 = DownloadManager.Request(Uri.parse(firma?.cer?.url))
                .setTitle("Descargando ${firma?.cer?.name}")
                .setDescription("Descargando ${firma?.cer?.name}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${firma?.cer?.name}")
            val request2 = DownloadManager.Request(Uri.parse(firma?.key?.url))
                .setTitle("Descargando ${firma?.key?.name}")
                .setDescription("Descargando ${firma?.key?.name}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${firma?.key?.name}")

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request1)
            downloadManager.enqueue(request2)
        }


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
                val firmaEliminar: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Firma_Electronica")
                firmaEliminar.whereEqualTo("objectId", firma?.objectId)
                try{
                    val firma = firmaEliminar.first
                    firma?.deleteInBackground(){ e ->
                        if(e==null){
                            onDataUpdateListener.onDataUpdate()
                            Toast.makeText(context, "Se ha eliminado la firma electronica",
                                Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Error al eliminar la firma electronica",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            builder.setNegativeButton("No") { _, _ ->

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