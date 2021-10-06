package ru.ccoders.clay.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.rest.TaskRest
import ru.ccoders.clay.services.MyReceiver.Companion.ID


class NotificationService : Service() {


    private val CANCELL_BUTTON_CODE = 100;
    private val COMPLETE_BUTTON_CODE = 101;
    var scheduleController: SQLScheduleController? = null
    private lateinit var nManager: NotificationManager
    private val TAG = "NotificationService"


    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        nManager = MyReceiver.createNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        val runId = intent.getIntExtra("final_id", 0);

        if (runId != 0) {
            Log.d("AHTUNG", "final Schedule")
            val scheduleRestController = TaskRest()
            if (scheduleController == null) {
                scheduleController = SQLScheduleController(this)
            }
            val id = intent.getIntExtra(ID, 0);
            val schedule = scheduleController!!.getScheduleById(id)
            val isRemote = schedule.getRemoteId() > 0
            val scope = CoroutineScope(Dispatchers.IO)
            if (runId == COMPLETE_BUTTON_CODE) {
                nManager.cancelAll()
                scheduleController!!.complete(id)
                if (isRemote) {
                    scope.async {
                        scheduleRestController.taskDone(schedule.getRemoteId())
                    }
                }
            } else if (runId == CANCELL_BUTTON_CODE) {
                nManager.cancelAll()
                scheduleController!!.cancel(id)
                if (isRemote) {
                    scope.async {
                        scheduleRestController.taskReject(schedule.getRemoteId())
                    }
                }
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
