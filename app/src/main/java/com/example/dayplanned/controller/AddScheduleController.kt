package com.example.dayplanned.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import com.example.dayplanned.model.Schedule
import java.lang.Exception
import java.sql.Time

class AddScheduleController(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private val DB_NAME = "plannedTime";
        private val TABLE_NAME = "schedule"
        private val DB_VERSION = 12
        private val ID = "id"
        private val HEADER = "header"
        private val DESCRIPTION = "description"
        private var TIME = "time"
        private var COMPLETED = "completed"
        private var SKIPPED = "skipped"

    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME ($ID Integer PRIMARY KEY, $HEADER TEXT, $DESCRIPTION TEXT, $TIME TEXT, $COMPLETED Integer, $SKIPPED Integer)"
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $TIME TIME");
            return
        }
        if (oldVersion == 2 or 3) {
            db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
            return
        }
        if(oldVersion < DB_VERSION){
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COMPLETED INTEGER")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $SKIPPED INTEGER")
        }
    }

    fun complete(id:Int){
        val  db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_NAME SET $COMPLETED =(Select $COMPLETED From $TABLE_NAME Where id = $id) + 1 WHERE id=$id");
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
        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(schedule.id.toString()))
        db.close()
        return (_success)
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
                val schedule = Schedule(id, header, desc,completed,skipped)
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