package mx.tecnm.cdhidalgo.aquaalert

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.util.Calendar

class Recordatorio : AppCompatActivity() {
    private lateinit var tvNewId: TextView
    private lateinit var etNewName: EditText
    private lateinit var etNewType: EditText
    private lateinit var etNewValue: EditText
    private lateinit var btnNewCancel: Button
    private lateinit var btnNewSave: Button

    private lateinit var etNewHour: EditText
    private val calendar = Calendar.getInstance()

    private lateinit var sesion: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordatorio)

        tvNewId = findViewById(R.id.tvNewId)
        etNewName = findViewById(R.id.etNewName)
        etNewType = findViewById(R.id.etNewType)
        etNewValue = findViewById(R.id.etNewValue)
        btnNewCancel = findViewById(R.id.btnNewCancel)
        btnNewSave = findViewById(R.id.btnNewSave)

        etNewHour = findViewById(R.id.etNewHour)

        etNewHour.setOnClickListener {
            mostrarTimePickerDialog()
        }

        sesion = getSharedPreferences("sesion", 0)

        if(supportActionBar != null) {
            supportActionBar!!.title = "Crear o modificar Recordatorios"
        }

        //al presionar el boton de cancelar se cierra la actividad y regresa a la anterior
        btnNewCancel.setOnClickListener { finish() }
        //si se reciben parametros se cargan en los campos de texto y se habilita el boton de guardar para actualizar
        if(intent.extras != null) {
            tvNewId.text = intent.extras?.getString("id")
            etNewName.setText(intent.extras?.getString("name"))
            etNewType.setText(intent.extras?.getString("desc"))
            etNewValue.setText(intent.extras?.getString("dose"))
            etNewHour.setText(intent.extras?.getString("hour"))
            btnNewSave.setOnClickListener { saveChanges() }
        }else{
            //si no se reciben parametros se habilita el boton de guardar para agregar un nuevo recordatorio
            btnNewSave.setOnClickListener { saveNew() }
        }
    }

    //funcion para agregar un nuevo recordatorio
    private fun saveNew() {
        //no lleva id dado que se creara un nuevo registro y el id se genera automaticamente
        val url = Uri.parse(Config.URL + "recordatorios")
            .buildUpon()
            .build().toString()

        val name = etNewName.text.toString()
        val desc = etNewType.text.toString()
        val dose = etNewValue.text.toString()
        val hour = etNewHour.text.toString()

        etNewHour.setOnClickListener {
            mostrarTimePickerDialog()
        }

        val body = JSONObject()
        body.put("name", name)
        body.put("desc", desc)
        body.put("dose", dose)
        body.put("hour", hour)

        /*if (name.isEmpty() || type.isEmpty() || value.isEmpty()) {
            return
        } */

        //se crear la peticion con POST para indicar al API que es un registro nuevo
        val peticion = object: JsonObjectRequest(Request.Method.POST, url, body, {
                response ->
            Toast.makeText(this, "Guardado"/*+response.toString()*/, Toast.LENGTH_LONG).show()
            val hourParts = hour.split(":")
            setAlarm(hourParts[0].toInt(), hourParts[1].toInt(), hourParts[2].toInt())
            finish()
        }, {
            error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                Log.e("token",  sesion.getString("jwt", "").toString())
                return body
            }
        }

        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    //funcion para actualizar un recordatorio
    private fun saveChanges() {
        //se crea la URL para hacer la peticion PUT al API REST agregando el id del recordatorio a actualizar tvNewId
        val url = Uri.parse(Config.URL + "recordatorios/" + tvNewId.text.toString())
            .buildUpon()
            .build().toString()

        //se obtienen los valores de los campos de texto
        val name = etNewName.text.toString()
        val desc = etNewType.text.toString()
        val dose = etNewValue.text.toString()
        val hour = etNewHour.text.toString()

        //se crea un objeto JSON con los valores de los campos de texto que se deben enviar al API REST
        val body = JSONObject()
        body.put("name", name)
        body.put("desc", desc)
        body.put("dose", dose)
        body.put("hour", hour)

        etNewHour.setOnClickListener {
            mostrarTimePickerDialog()
        }

        /*si algun campo esta vacio no se hace nada
        if (name.isEmpty() || type.isEmpty() || value.isEmpty()) {
            return
        } */
        //se crea la peticion con JsonObjectRequest para realizar el PUT con los datos de JSONObject body a enviar
        //el API REST regresa un JSON con los datos del senson actualizado, por lo que se puede usar JsonObjectRequest o StringRequest
        //sin embargo la forma de enviar los datos cambia en el StringRequest, ver el ejemplo del login
        val peticion = object: JsonObjectRequest(Request.Method.PUT, url, body, {
            response ->
            val hourParts = hour.split(":")
            setAlarm(hourParts[0].toInt(), hourParts[1].toInt(), hourParts[2].toInt())
                //Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()
                finish()
        }, {
            error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            Log.e("token",  error.stackTraceToString())
        }){
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString()
                Log.e("token",  sesion.getString("jwt", "").toString())
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun mostrarTimePickerDialog() {
        val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
        val minutoActual = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hora, minuto ->
                // Aquí obtienes la hora seleccionada
                // Puedes hacer lo que necesites con ella (por ejemplo, guardarla en una variable)
                // hora y minuto son los valores seleccionados
                val horaFormateada = String.format("%02d:%02d:00", hora, minuto)
                etNewHour.setText(horaFormateada)
            },
            horaActual,
            minutoActual,
            true // Si quieres usar el formato de 24 horas, ponlo en true
        )

        timePickerDialog.show()
    }

    private fun setAlarm(hour: Int, minute: Int, second:Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Configura la alarma para que se dispare a la hora especificada
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }

        // Configura la alarma para que se repita todos los días a la misma hora
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
