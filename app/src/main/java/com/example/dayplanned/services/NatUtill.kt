import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NotificationUtils(base: Context) : ContextWrapper(base) {

    val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val myChannel = NotificationChannel(CHANNEL_ID,
            CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "AHTUNG NOTIFICATION"
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(500,50,500)
            setShowBadge(false)
        }

        nManager.createNotificationChannel(myChannel)
    }

    companion object {
        const val CHANNEL_ID = "schedule"
        const val CHANNEL_NAME = "DailyPlannedNotification"
    }
}