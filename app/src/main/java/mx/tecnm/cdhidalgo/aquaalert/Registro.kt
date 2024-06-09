package mx.tecnm.cdhidalgo.aquaalert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout

class Registro : AppCompatActivity() {

    private lateinit var btnRegistrarme:Button
    private lateinit var btnYaEstoyRegistrado:Button

    private lateinit var nombre:TextInputLayout
    private lateinit var apellidos:TextInputLayout
    private lateinit var carrera:Spinner
    private lateinit var correo:TextInputLayout
    private lateinit var pass:TextInputLayout
    private lateinit var rol:String

    var _carrera = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //Acceso a la base de datos

        btnRegistrarme = findViewById(R.id.btnRegistrarme)
        btnYaEstoyRegistrado = findViewById(R.id.btnYaEstoyRegistrado)
        nombre = findViewById(R.id.nombre)
        apellidos = findViewById(R.id.apellidos)
        correo = findViewById(R.id.email_registro)
        pass = findViewById(R.id.password_registro)
        rol = "comun"

        btnRegistrarme.setOnClickListener{}

        btnYaEstoyRegistrado.setOnClickListener{
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
    }

}