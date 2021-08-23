package ru.ccoders.clay.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import ru.ccoders.clay.model.ScheduleModel
import org.json.JSONArray
import java.lang.Exception
import java.sql.Time

class SQLScheduleController(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private val DB_NAME = "plannedTime";
        private val TABLE_NAME = "schedule"
        private val DB_VERSION = 14
        private val ID = "id"
        private val HEADER = "header"
        private val DESCRIPTION = "description"
        private val TIME = "time"
        private val COMPLETED = "completed"
        private val SKIPPED = "skipped"
        private val REMOTE_ID = "remote_id"
        private var EDITABLE_TIME = "editable_time"
        var MODE = "mode"
        var DAILY_MODE = 1
        var VEEKLY_MODE = 2
        var SCHEDULE = "schedule"
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
                    " $REMOTE_ID Integer,"+
                    " $SCHEDULE String)"
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

//        if(12 < DB_VERSION){
//            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $MODE INTEGER")
//            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $SCHEDULE String")
//        }
        if(13 < DB_VERSION){
            db!!.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $EDITABLE_TIME BOOLEAN DEFAULT TRUE")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $REMOTE_ID DOUBLE")
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

    fun addSchedule(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("header", scheduleModel.header)
        values.put("description", scheduleModel.description)
        val _success = db.insert(TABLE_NAME, null, values);
        db.close();
        scheduleModel.id = _success.toInt()
        Log.v("InsertedID", "$_success")
        return Integer.parseInt("$_success")
    }

    fun updateSchedule(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        cv.put(HEADER, scheduleModel.header)
        cv.put(DESCRIPTION, scheduleModel.description)
        cv.put(MODE,scheduleModel.mode)
        cv.put(SCHEDULE,scheduleModel.schedule.toString())

        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(scheduleModel.id.toString()))
        db.close()
        return (_success)
    }

    fun setTime(scheduleModel: ScheduleModel): Int {
        val db = this.writableDatabase
        val cv = ContentValues();
        if(scheduleModel.time == null){
            return 0;
        }
        cv.put(TIME,scheduleModel.getTxtTime())
        cv.put(MODE,scheduleModel.mode)
        cv.put(SCHEDULE,scheduleModel.schedule.toString())
        cv.put(REMOTE_ID,scheduleModel.getRemoteId())

        val _success = db.update(TABLE_NAME, cv, "$ID = ?", arrayOf(scheduleModel.id.toString()))
        db.close()
        return (_success)
    }
    fun getScheduleById(id: Int): ScheduleModel{
        val db = readableDatabase
        val selectAll = "Select * from $TABLE_NAME WHERE id= $id";
        val cursor = db.rawQuery(selectAll, null);
        var result: ScheduleModel? = null;
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
                val remoteId = cursor.getInt(cursor.getColumnIndex(REMOTE_ID))
                if(s == null){
                    s = "[]"
                }
                val arra = JSONArray(s)
                val schedule = ScheduleModel(id, header, desc,completed,skipped,mode,arra)
                schedule.setRemoteId(remoteId)
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
    fun getSchedule(): List<ScheduleModel> {
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
                Log.d("AddScheduleController","gs"+cursor.getColumnIndex(MODE))
                val mode = cursor.getInt(cursor.getColumnIndex(MODE))
                var s = cursor.getString(cursor.getColumnIndex(SCHEDULE))
                val remoteId  = cursor.getInt(cursor.getColumnIndex(REMOTE_ID))
                if(s == null){
                    s = "[]"
                }
                val arra = JSONArray(s)
                val schedule = ScheduleModel(id, header, desc,completed,skipped,mode,arra)
                schedule.setRemoteId(remoteId)
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
                scheduleModels.add(schedule)
            }
        }
        cursor.close();
        db.close();
        return scheduleModels;
    }

    fun delSchedule(id: Int) {
        val db = readableDatabase
        db.delete(TABLE_NAME, "$ID = ?", arrayOf(id.toString()))
        db.close()
    }
}