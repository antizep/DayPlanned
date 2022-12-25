package ru.ccoders.clay.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import ru.ccoders.clay.dto.ScheduleModel
import org.json.JSONArray
import java.lang.Exception
import java.sql.Time
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SQLiteScheduleController(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val TAG = SQLiteScheduleController::class.java.name

    companion object {
        private val DB_NAME = "plannedTime";
        private val TABLE_NAME = "schedule"
        private val DB_VERSION = 15
        private val ID = "id"
        private val HEADER = "header"
        private val DESCRIPTION = "description"
        private var TIME = "time"
        private var COMPLETED = "completed"
        private var SKIPPED = "skipped"
        private val REMOTE_ID = "remote_id"
        private var COMPLETE_DATE = "complete_date"
        val MODE = "mode"
        val DAILY_MODE = 1
        val VEEKLY_MODE = 2
        val SCHEDULE = "schedule"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME ($ID Integer PRIMARY KEY," +
                    " $HEADER TEXT," +
                    " $DESCRIPTION TEXT," +
                    " $TIME TEXT," +
                    " $COMPLETED Integer," +
                    " $SKIPPED Integer," +
                    " $MODE Integer," +
                    " $REMOTE_ID BIGINT," +
                    " $SCHEDULE String," +
                    " $COMPLETE_DATE String)"
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if (oldVersion <= 12) {
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $MODE INTEGER")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $SCHEDULE String")
        } else if (oldVersion <= 13) {
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $REMOTE_ID BIGINT")
        } else if (oldVersion <= 14) {
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COMPLETE_DATE String")
        }
    }

    @SuppressLint("Range")
    fun complete(id: Int) {
        val db = this.writableDatabase
        var localDate = nowDate()
        val cursor = db.rawQuery("Select $COMPLETED From $TABLE_NAME Where id = $id", null);
        var c = 0;
        if (cursor != null && cursor.moveToNext()) {
            c = cursor.getInt(cursor.getColumnIndex(COMPLETED));
        }
        c++
        db.execSQL("UPDATE $TABLE_NAME SET $COMPLETED = $c, $COMPLETE_DATE = '$localDate' WHERE id = $id");
    }

    @SuppressLint("Range")
    fun cancel(id: Int) {
        val db = this.writableDatabase
        val cursor = db.rawQuery("Select $SKIPPED From $TABLE_NAME Where id = $id", null);
        var c = 0;
        var localDate = nowDate();
        if (cursor != null && cursor.moveToNext()) {
            c = cursor.getInt(cursor.getColumnIndex(SKIPPED));
        }
        c++
        db.execSQL("UPDATE $TABLE_NAME SET $SKIPPED = $c,$COMPLETE_DATE = '$localDate' WHERE id = $id");
    }

    fun addSchedule(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("header", scheduleModel.header)
        values.put("description", scheduleModel.description)
        val _success = db.insert(TABLE_NAME, null, values);
        db.close();
        Log.v("InsertedID", "$_success")
        return Integer.parseInt("$_success")
    }

    fun updateSchedule(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        cv.put(HEADER, scheduleModel.header)
        cv.put(DESCRIPTION, scheduleModel.description)
        cv.put(MODE, scheduleModel.mode)
        cv.put(SCHEDULE, scheduleModel.schedule.toString())
        cv.put(REMOTE_ID, scheduleModel.getRemoteId())
        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(scheduleModel.id.toString()))
        db.close()
        return (_success)
    }

    fun updateScheduleByRemoteId(scheduleModel: ScheduleModel): Int {
        val d = getScheduleById(scheduleModel.getRemoteId(), true)
        if (d != null) {
            scheduleModel.id = d.id
        } else {
            scheduleModel.id = addSchedule(scheduleModel)
        }

        val db = this.writableDatabase
        val cv = ContentValues();
        cv.put(HEADER, scheduleModel.header)
        cv.put(DESCRIPTION, scheduleModel.description)
        cv.put(MODE, scheduleModel.mode)
        cv.put(SCHEDULE, scheduleModel.schedule.toString())
        cv.put(REMOTE_ID, scheduleModel.getRemoteId())
        cv.put(TIME, scheduleModel.getTxtTime())
        val _success = db.update(
            TABLE_NAME,
            cv,
            "$ID = ?",
            arrayOf(scheduleModel.id.toString())
        )
        db.close()
        Log.d("schedules - ", getSchedule().toString())
        return (_success)
    }

    fun setTime(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        if (scheduleModel.time == null) {
            return 0;
        }
        cv.put(TIME, scheduleModel.getTxtTime())
        cv.put(MODE, scheduleModel.mode)
        cv.put(SCHEDULE, scheduleModel.schedule.toString())

        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(scheduleModel.id.toString()))
        db.close()
        return (_success)
    }

    fun getScheduleById(id: Int): ScheduleModel {
        return getScheduleById(id.toLong(), false)!!
    }

    @SuppressLint("Range")
    fun getScheduleById(id: Long, isRemote: Boolean): ScheduleModel? {
        val db = readableDatabase
        val selectAll = if (isRemote) {
            "Select * from $TABLE_NAME WHERE $REMOTE_ID= $id";
        } else {
            "Select * from $TABLE_NAME WHERE $ID= $id";
        }
        val cursor = db.rawQuery(selectAll, null);
        var result: ScheduleModel? = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val header = cursor.getString(cursor.getColumnIndex(HEADER))
                val desc = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                val time = cursor.getString(cursor.getColumnIndex(TIME));
                val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
                val skipped = cursor.getInt(cursor.getColumnIndex(SKIPPED))
                Log.d("AddScheduleController", "gs" + cursor.getColumnIndex(MODE))
                val mode = cursor.getInt(cursor.getColumnIndex(MODE))
                val completeDate = cursor.getString(cursor.getColumnIndex(COMPLETE_DATE))
                var s = cursor.getString(cursor.getColumnIndex(SCHEDULE))
                if (s == null) {
                    s = "[]"
                }
                val idInt = cursor.getInt(cursor.getColumnIndex(ID))

                val remoteId = cursor.getLong(cursor.getColumnIndex(REMOTE_ID))
                val arra = JSONArray(s)
                val schedule = ScheduleModel(
                    idInt,
                    header,
                    desc,
                    completed,
                    skipped,
                    mode,
                    parseDate(completeDate),
                    arra
                )

                schedule.setRemoteId(remoteId)
                if (!time.isNullOrBlank()) {
                    try {
                        val time = Time.valueOf(time)
                        schedule.time = Calendar.getInstance();
                        schedule.time!!.set(Calendar.HOUR_OF_DAY, time.hours)
                        schedule.time!!.set(Calendar.MINUTE, time.minutes)
                        schedule.time!!.set(Calendar.SECOND, 0)
                    } catch (e: Exception) {

                    }
                }
                result = schedule

            }
        }
        cursor.close();
        db.close();
        return result
    }

    @SuppressLint("Range")
    fun getSchedule(): MutableList<ScheduleModel> {
        val scheduleModels: MutableList<ScheduleModel> = mutableListOf()
        val db = readableDatabase;
        val selectAll = "Select * from $TABLE_NAME ";
        val cursor = db.rawQuery(selectAll, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val header = cursor.getString(cursor.getColumnIndex(HEADER))
                val desc = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                val time = cursor.getString(cursor.getColumnIndex(TIME));
                val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
                val skipped = cursor.getInt(cursor.getColumnIndex(SKIPPED))
                val remoteId = cursor.getLong(cursor.getColumnIndex(REMOTE_ID))
                val completeDate = cursor.getString(cursor.getColumnIndex(COMPLETE_DATE))
                Log.d("AddScheduleController", "gs" + cursor.getColumnIndex(MODE))
                val mode = cursor.getInt(cursor.getColumnIndex(MODE))
                var s = cursor.getString(cursor.getColumnIndex(SCHEDULE))
                if (s == null) {
                    s = "[]"
                }
                val arra = JSONArray(s)
                val schedule = ScheduleModel(
                    id,
                    header,
                    desc,
                    completed,
                    skipped,
                    mode,
                    parseDate(completeDate),
                    arra
                )
                schedule.setRemoteId(remoteId)
                if (!time.isNullOrBlank()) {
                    try {
                        val time = Time.valueOf(time)
                        schedule.time = Calendar.getInstance();
                        schedule.time!!.set(Calendar.HOUR_OF_DAY, time.hours)
                        schedule.time!!.set(Calendar.MINUTE, time.minutes)
                        schedule.time!!.set(Calendar.SECOND, 0)
                    } catch (e: Exception) {

                    }
                }
                scheduleModels.add(schedule)
            }
        }
        cursor.close();
        db.close();
        return scheduleModels;
    }

    fun parseDate(date: String?): LocalDate? {
        Log.d(TAG, "date:" + date)
        return if (date != null)
            LocalDate.parse(
                date,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
        else null;
    }

    fun nowDate(): String {
        val d = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        Log.d(TAG, "nowDate:" + d)
        return d
    }

    fun dropRemoteIdSchedules() {
        val db = readableDatabase

        db.delete(TABLE_NAME, "$REMOTE_ID > ?", arrayOf("0"))
        db.close()
    }

    fun delSchedule(id: Int) {
        val db = readableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }
}