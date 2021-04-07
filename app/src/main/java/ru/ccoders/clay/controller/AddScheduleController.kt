package ru.ccoders.clay.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import ru.ccoders.clay.model.Schedule
import org.json.JSONArray
import java.lang.Exception
import java.sql.Time

class AddScheduleController(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private val DB_NAME = "plannedTime";
        private val TABLE_NAME = "schedule"
        private val DB_VERSION = 13
        private val ID = "id"
        private val HEADER = "header"
        private val DESCRIPTION = "description"
        private var TIME = "time"
        private var COMPLETED = "completed"
        private var SKIPPED = "skipped"
        public var MODE = "mode"
        public var DAILY_MODE = 1
        public var VEEKLY_MODE = 2
        public var SCHEDULE = "schedule"
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
                    " $SCHEDULE String)"
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if(12 < DB_VERSION){
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $MODE INTEGER")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $SCHEDULE String")
        }
    }

    fun complete(id:Int){
        val  db = this.writableDatabase
        val cursor = db.rawQuery("Select $COMPLETED From $TABLE_NAME Where id = $id",null);
        var c  = 0;
        if (cursor != null && cursor.moveToNext()) {
            c  = cursor.getInt(cursor.getColumnIndex(COMPLETED));
        }
        c++
        db.execSQL("UPDATE $TABLE_NAME SET $COMPLETED = $c WHERE id = $id");
    }

    fun cancel(id:Int){
        val  db = this.writableDatabase
        val cursor = db.rawQuery("Select $SKIPPED From $TABLE_NAME Where id = $id",null);
        var c  = 0;
        if (cursor != null && cursor.moveToNext()) {
            c  = cursor.getInt(cursor.getColumnIndex(SKIPPED));
        }
        c++
        db.execSQL("UPDATE $TABLE_NAME SET $SKIPPED = $c WHERE id = $id");
    }

    fun addSchedule(schedule: Schedule): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("header", schedule.header)
        values.put("description", schedule.description)
        val _success = db.insert(TABLE_NAME, null, values);
        db.close();
        Log.v("InsertedID", "$_success")
        return Integer.parseInt("$_success")
    }

    fun updateSchedule(schedule: Schedule): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        cv.put(HEADER, schedule.header)
        cv.put(DESCRIPTION, schedule.description)
        cv.put(MODE,schedule.mode)
        cv.put(SCHEDULE,schedule.schedule.toString())

        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(schedule.id.toString()))
        db.close()
        return (_success)
    }

    fun setTime(schedule: Schedule): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        if(schedule.time == null){
            return 0;
        }
        cv.put(TIME,schedule.getTxtTime())
        cv.put(MODE,schedule.mode)
        cv.put(SCHEDULE,schedule.schedule.toString())

        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(schedule.id.toString()))
        db.close()
        return (_success)
    }
    fun getScheduleById(id: Int): Schedule{
        val db = readableDatabase
        val selectAll = "Select * from $TABLE_NAME WHERE id= $id";
        val cursor = db.rawQuery(selectAll, null);
        var result: Schedule? = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val header = cursor.getString(cursor.getColumnIndex(HEADER))
                val desc = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                val time = cursor.getString(cursor.getColumnIndex(TIME));
                val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
                val skipped = cursor.getInt(cursor.getColumnIndex(SKIPPED))
                Log.d("AddScheduleController","gs"+cursor.getColumnIndex(MODE))
                val mode = cursor.getInt(cursor.getColumnIndex(MODE))
                var s = cursor.getString(cursor.getColumnIndex(SCHEDULE))
                if(s == null){
                    s = "[]"
                }
                val arra = JSONArray(s)
                val schedule = Schedule(id, header, desc,completed,skipped,mode,arra)
                if(!time.isNullOrBlank()) {
                    try {
                        val time = Time.valueOf(time)
                        schedule.time = Calendar.getInstance();
                        schedule.time!!.set(Calendar.HOUR_OF_DAY,time.hours)
                        schedule.time!!.set(Calendar.MINUTE,time.minutes)
                        schedule.time!!.set(Calendar.SECOND,0)
                    }catch (e:Exception){

                    }
                }
                result = schedule

            }
        }
        cursor.close();
        db.close();
        return result!!
    }
    fun getSchedule(): MutableList<Schedule> {
        val schedules: MutableList<Schedule> = mutableListOf()
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
                Log.d("AddScheduleController","gs"+cursor.getColumnIndex(MODE))
                val mode = cursor.getInt(cursor.getColumnIndex(MODE))
                var s = cursor.getString(cursor.getColumnIndex(SCHEDULE))
                if(s == null){
                    s = "[]"
                }
                val arra = JSONArray(s)
                val schedule = Schedule(id, header, desc,completed,skipped,mode,arra)
                if(!time.isNullOrBlank()) {
                    try {
                        val time = Time.valueOf(time)
                        schedule.time = Calendar.getInstance();
                        schedule.time!!.set(Calendar.HOUR_OF_DAY,time.hours)
                        schedule.time!!.set(Calendar.MINUTE,time.minutes)
                        schedule.time!!.set(Calendar.SECOND,0)
                    }catch (e:Exception){

                    }
                }
                schedules.add(schedule)
            }
        }
        cursor.close();
        db.close();
        return schedules;
    }

    fun delSchedule(id: Int) {
        val db = readableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }
}