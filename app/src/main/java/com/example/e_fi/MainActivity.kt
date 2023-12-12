package com.example.e_fi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery

class MainActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        username = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            login()
        }
        /*val firstObject = ParseObject("FirstClass")
        firstObject.put("message","Hey ! First message from android. Parse is now connected")
        firstObject.saveInBackground {
            if (it != null){
                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
            }else{
                Log.d("MainActivity","Object saved.")
            }
        }*/
    }
    fun login() {
        val username = username.text.toString()
        val password = password.text.toString()
        val usr:ParseQuery<ParseObject> = ParseQuery.getQuery("usr")
        usr.whereEqualTo("name",username)
        usr.getFirstInBackground(object : GetCallback<ParseObject> {
            override fun done(User: ParseObject?, e: ParseException?) {
                if (e == null && User != null) {
                    val storedPassword = User.getString("passwd")
                    // Asume que la contraseña se almacena en una columna llamada "password"

                    // Ahora compara la contraseña ingresada con la almacenada
                    val enteredPassword = password

                    if (enteredPassword == storedPassword) {
                        // La contraseña es correcta, realiza las acciones necesarias
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso",
                            Toast.LENGTH_SHORT).show()
                        // Puedes redirigir a la siguiente actividad aquí
                    } else {
                        // La contraseña es incorrecta
                        Toast.makeText(this@MainActivity, "Contraseña incorrecta",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Algo salió mal al recuperar el objeto del jugador
                    Toast.makeText(this@MainActivity, "Usuario incorrecto",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}