package com.example.dayplanned.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.dayplanned.model.Schedule

class AddScheduleController(context: Context):
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM){
     override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID Integer PRIMARY KEY, $HEADER TEXT, $DESCRIPTION TEXT)"
        db?.execSQL(CREATE_TABLE)
    }


    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        private val DB_NAME = "plannedTime";
        private val TABLE_NAME = "schedule"
        private val DB_VERSIOM = 1
        private val ID = "id"
        private val HEADER = "header"
        private val DESCRIPTION = "description"

    }

    fun addSchedule(schedule : Schedule): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("header", schedule.header)
        values.put("description",schedule.description)
        val _success = db.insert(TABLE_NAME,null,values);
        db.close();
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }
    fun getSchedule(): MutableList<Schedule> {
        val schedules: MutableList<Schedule> = mutableListOf()
        val db =readableDatabase;
        val selectAll = "Select * from $TABLE_NAME ";
        val cursor  = db.rawQuery(selectAll,null);
        if(cursor !=null){
            while (cursor.moveToNext()){
                    var id = cursor.getString(cursor.getColumnIndex(ID))
                    var header = cursor.getString(cursor.getColumnIndex(HEADER))
                    var desc = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                    schedules.add(Schedule(id.toInt(),header,desc))
            }
        }
        cursor.close();
        db.close();
        return schedules;
    }
}