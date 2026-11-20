package com.hernandez.studentinfosystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentSIS_Themed.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)");

        db.execSQL("CREATE TABLE students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_number TEXT, " +
                "last_name TEXT, " +
                "first_name TEXT, " +
                "middle_name TEXT, " +
                "program TEXT, " +
                "specialization TEXT, " +
                "year_level TEXT)");

        // Default admin account
        db.execSQL("INSERT INTO users (username, password) VALUES ('admin', 'admin123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS students");
        onCreate(db);
    }

    // --- AUTH METHODS ---
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // --- STUDENT METHODS ---
    public boolean insertStudent(String studentNumber, String lastName, String firstName,
                                 String middleName, String program, String specialization, String yearLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_number", studentNumber);
        values.put("last_name", lastName);
        values.put("first_name", firstName);
        values.put("middle_name", middleName);
        values.put("program", program);
        values.put("specialization", specialization);
        values.put("year_level", yearLevel);
        return db.insert("students", null, values) != -1;
    }

    public List<String> getAllStudentsFormatted() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM students ORDER BY last_name ASC", null);

        if (cursor.moveToFirst()) {
            do {
                String studNum = cursor.getString(1);
                String lName = cursor.getString(2);
                String fName = cursor.getString(3);
                String mName = cursor.getString(4);
                String prog = cursor.getString(5);
                String spec = cursor.getString(6);
                String year = cursor.getString(7);

                String midInitial = (mName != null && !mName.isEmpty()) ? " " + mName.charAt(0) + "." : "";
                String specDisplay = (spec != null && !spec.isEmpty()) ? " (" + spec + ")" : "";

                String record = lName + ", " + fName + midInitial + "\n" +
                        "ID: " + studNum + "  |  " + prog + specDisplay + " • Year " + year;
                list.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteStudentByNumber(String studentNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("students", "student_number = ?", new String[]{studentNumber});
    }
}
