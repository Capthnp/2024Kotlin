package com.example.lab_8911

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "StudentDB"
        private val DB_VERSION = 1
        private val TABLE_NAME = "student"
        private val COLUMN_ID = "id"
        private val COLUMN_NAME = "name"
        private val COLUMN_GENDER = "gender"
        private val COLUMN_AGE = "age"

        private var sqliteHelper: DatabaseHelper? = null

        @Synchronized
        fun getInstance(c: Context): DatabaseHelper {
            if (sqliteHelper == null) {
                sqliteHelper = DatabaseHelper(c.applicationContext)
            }
            return sqliteHelper!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, " + "$COLUMN_GENDER TEXT, " + "$COLUMN_AGE TEXT)"
        db?.execSQL(CREATE_TABLE)

//        val sqlInsert = "INSERT INTO $TABLE_NAME VALUES('65000000-1','Alice','Female',20)"
//        db?.execSQL(sqlInsert)

        val sqlInsert = "INSERT INTO $TABLE_NAME VALUES('65000000-2','Bobby','Other',21)"
        db?.execSQL(sqlInsert)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Query all students
    @SuppressLint("Range")
    fun getAllStudents(): ArrayList<Student> {
        val studentList = ArrayList<Student>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        } catch (e: SQLiteException) {
            onCreate(db)
            return ArrayList()
        }

        var id: String
        var name: String
        var gender: String
        var age: Int

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
                name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                gender = cursor.getString(cursor.getColumnIndex(COLUMN_GENDER))
                age = cursor.getInt(cursor.getColumnIndex(COLUMN_AGE))
                studentList.add(Student(id, name, gender, age))
                cursor.moveToNext()
            }
        }
        db.close()
        return studentList
    }

    // Insert student
    fun insertStudent(std: Student): Long {
        val db = writableDatabase
        val value = ContentValues()
        value.put(COLUMN_ID, std.std_id)
        value.put(COLUMN_NAME, std.std_name)
        value.put(COLUMN_GENDER, std.std_gender)
        value.put(COLUMN_AGE, std.std_age)
        val success = db.insert(TABLE_NAME, null, value)
        db.close()
        return success
    }

    // Update student
    fun updateStudent(std: Student): Int {
        val db = writableDatabase
        val value = ContentValues()
        value.put(COLUMN_NAME, std.std_name)
        value.put(COLUMN_GENDER, std.std_gender)
        value.put(COLUMN_AGE, std.std_age)
        val success = db.update(TABLE_NAME, value, "$COLUMN_ID = ?", arrayOf(std.std_id))
        db.close()
        return success
    }

    // Delete student
    fun deleteStudent(std_id: String): Int {
        val db = writableDatabase
        val success = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(std_id))
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getStudent(stdId: String): Student? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id = ?", arrayOf(stdId))

        var student: Student? = null
        if (cursor.moveToFirst()) {
            student = Student(
                cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_AGE))
            )
        }
        cursor.close()
        db.close()
        return student
    }
    fun countStudents(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT($COLUMN_ID) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

}