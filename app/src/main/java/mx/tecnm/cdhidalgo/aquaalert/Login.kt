package mx.tecnm.cdhidalgo.aquaalert

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

//import com.android.volley.Request

class Login : AppCompatActivity() {
    //declaracion de variables para los elementos de la vista
    lateinit var etLoginUser: TextInputLayout
    lateinit var etLoginPass: TextInputLayout
    lateinit var btnLoginStart: MaterialButton
    lateinit var sesion: SharedPreferences

    private lateinit var btnRegistrar : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnRegistrar = findViewById(R.id.btnRegistrar)

        //inicializacion de la variable sesion con el archivo de preferencias llamado "sesion"
        sesion = getSharedPreferences("sesion", 0)

        // Verificar si la sesión ya existe
        val jwt = sesion.getString("jwt", null)
        if (jwt != null) {
            // Si el token jwt no es nulo, entonces la sesión ya existe.
            // Redirigir al usuario a la actividad Principal
            startActivity(Intent(this, Principal::class.java))
            finish() // Finalizar la actividad de inicio de sesión
            return
        }

        //inicializacion de las variables con los elementos de la vista
        etLoginUser = findViewById(R.id.etLoginUser)
        etLoginPass = findViewById(R.id.etLoginPass)
        btnLoginStart = findViewById(R.id.btnLoginStart)

        btnLoginStart.setOnClickListener { login() }

        btnRegistrar.setOnClickListener{
            val intent = Intent(this,Registro::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val email = etLoginUser.editText?.text
        val psw = etLoginPass.editText?.text
        //url de la peticion del login
        val url = Uri.parse(Config.URL + "login")
            .buildUpon()
            .build().toString()
        //debe crearse una peticion de tipo StringRequest con el metodo POST para la url de login debido al que el servidor siempre devulve un string con el token
        val peticion = object:StringRequest(/*Request.*/Method.POST, url, {
            //si la peticion es exitosa el token esta en response
            response ->
                //se guarda el token "jwt" y el nombre de usuario en el archivo de preferencias
                with(sesion.edit()) {
                    putString("jwt", response)
                    putString("username", email.toString())
                    apply()
                }
                //se inicia la actividad MainActivity2
                startActivity(Intent(this, Principal::class.java))
        }, { error ->
                //si la peticion falla se muestra un mensaje de error
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                //se muestra la traza completa del error en el Logcat
                Log.e("LOGIN", error.stackTraceToString())
        }){
            //se envian los datos de la peticion usuario y contraseña para el post
            override fun getParams(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["username"] = email.toString()
                body.put("password", psw.toString())
                return body
            }
        }
        //se agrega la peticion a la cola de peticiones para que sea procesada
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    override fun onBackPressed() {
    }
}




