package mx.tecnm.cdhidalgo.aquaalert

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.sql.DriverManager

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {

        // Crear un canal de notificación para Android 8.0 y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MiCanal"
            val descriptionText = "Mi Canal de Notificación"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("miCanal", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val builder = NotificationCompat.Builder(context, "miCanal")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Reemplaza esto con tu propio ícono
            .setContentTitle("Recordatorio")
            .setContentText("Es hora de tomar sus medicamentos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        // Mostrar la notificación
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, builder.build())
        }
    }
}
