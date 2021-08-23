package ru.ccoders.clay.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.services.MyReceiver.Companion.ID


class NotificationService : Service() {


    private val CANCELL_BUTTON_CODE = 100;
    private val COMPLETE_BUTTON_CODE = 101;
    var scheduleController: SQLScheduleController? = null
    private lateinit var nManager: NotificationManager
    private val TAG = "NotificationService"


    override fun onCreate() {
        Log.d(TAG,"onCreate()")
        nManager = MyReceiver.createNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand()")

        val runId = intent.getIntExtra("final_id", 0);

        if (runId != 0) {
            Log.d("AHTUNG", "final Schedule")
            if (runId == COMPLETE_BUTTON_CODE) {
                nManager.cancelAll()
                if (scheduleController == null) {
                    scheduleController = SQLScheduleController(this)
                }
                scheduleController!!.complete(intent.getIntExtra(ID, 0))
            } else if (runId == CANCELL_BUTTON_CODE) {
                nManager.cancelAll()
                if (scheduleController == null) {
                    scheduleController = SQLScheduleController(this)
                }
                scheduleController!!.cancel(intent.getIntExtra(ID, 0))
            }

        }
        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
