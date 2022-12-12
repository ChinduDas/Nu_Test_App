package com.mechine_test.nu_test_app.db


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mechine_test.nu_test_app.models.Device

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY, " +
                DEV_NAME + " TEXT," + DEV_STATUS + " TEXT," +
                DEV_IP + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveDevice(device: Device): Boolean {
        return try {
            if(getDevice(device.devName!!)==null){
                val values = ContentValues()
                values.put(DEV_NAME, device.devName)
                values.put(DEV_IP, device.devIp)
                values.put(DEV_STATUS, device.status)
                val db = this.writableDatabase
                db.insert(TABLE_NAME, null, values)
                db.close()
            }else{
                updateDevice(device.devName.toString())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    @SuppressLint("Range")
    fun getDevice(name : String): Device? {
        val db = this.readableDatabase
        val data =  db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $DEV_NAME = '$name'", null)
        data.moveToFirst()
        if(data.count==0)
            return null
        val device  = Device(data.getString(data.getColumnIndex(DEV_NAME)) ,data.getString(data.getColumnIndex(DEV_IP)) )
        data.close()
        return device
    }

    @SuppressLint("Range")
    fun getDevices(): ArrayList<Device>? {
        val db = this.readableDatabase
        val data =  db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val dataList = ArrayList<Device>()
        while(data.moveToNext()){
            dataList.add(Device(data.getString(data.getColumnIndex(DEV_NAME)) ,data.getString(data.getColumnIndex(DEV_IP)),data.getString(data.getColumnIndex(
                DEV_STATUS)) ))
        }
        data.close()
        return dataList
    }
    @SuppressLint("Recycle")
    fun deleteDevices(): Boolean? {
        return try {
            val db = this.readableDatabase
            db.delete(TABLE_NAME,null,null)
            true
        }catch (e : Exception){
            false
        }
    }
    @SuppressLint("Recycle")
    fun updateDevice(name: String): Boolean? {
        val updateData  = ContentValues()
        updateData.put(DEV_STATUS , "1")
        return try {
            val db = this.readableDatabase
            db.update(TABLE_NAME,updateData, "$DEV_NAME = '$name'",null)
            true
        }catch (e : Exception){
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("Recycle")
    fun updateDevices(): Boolean? {
        val updateData  = ContentValues()
        updateData.put(DEV_STATUS , "0")
        return try {
            val db = this.readableDatabase
            db.update(TABLE_NAME,updateData, null,null)
            true
        }catch (e : Exception){
            false
        }
    }

    companion object {
        private val DATABASE_NAME = "NU_TEST_DB"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "DiscoveredDevices"
        val ID = "id"
        val DEV_NAME = "deviceName"
        val DEV_IP = "deviceIp"
        val DEV_STATUS = "devStatus"
    }
}
