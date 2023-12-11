package com.example.pi.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pi.Transformations;
import com.example.pi.databases.tables.Result;
import com.example.pi.data.TestTakerInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//old!! don't use in project (usages to don't using namespace)

public class DBAccess {

    private static final boolean HAVE_SAME_NAME = false, NO_HAVE_SAME_NAME = true;
    private static final int NO_ERRORS = 0, NO_GROUP = 1, HAVE_SAME_KANJI_IN_GROUP = 2;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;

    public DBAccess(Context context) {
        dbHelper = new DBHelper(context);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public List<String> getAreasOfActivity() {
        List<String> areas = new ArrayList<>();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM AREAS_OF_ACTIVITY ORDER BY ID", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                areas.add(cursor.getString(1));
                cursor.moveToNext();
            }
        cursor.close();
        return areas;
    }

    public List<Result> getResults(String testTaker) {
        List<Result> results = new ArrayList<Result>();
        int counter = 0;
        cursor = sqLiteDatabase.rawQuery("SELECT RATING, TIME, DATE FROM RESULTS WHERE TEST_TAKER = '" + testTaker + "'", null);
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                results.add(new Result());
                results.get(counter).rating = Integer.parseInt(cursor.getString(0));
                results.get(counter).time = cursor.getString(1);
                results.get(counter).date = cursor.getString(2);
                counter++;
                cursor.moveToNext();
            }
        cursor.close();
        return results;
    }

    public int addNewAnonymous(TestTakerInformation testTakerInf) {
        int id = -1;
        cursor = sqLiteDatabase.rawQuery("SELECT ID FROM ANONYMOUS_INFORMATION ORDER BY ID DESC LIMIT 1", null);
        if (cursor.moveToFirst())
            id = Integer.parseInt(cursor.getString(0)) + 1;
        else
            id = 1;
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("AGE", testTakerInf.age);
        contentValues.put("AREA", testTakerInf.area);
        contentValues.put("GENDER", testTakerInf.area);
        contentValues.put("IS_SCHOOLCHILD", Transformations.BooleanToInteger(testTakerInf.isSchoolchild));
        contentValues.put("IS_STUDENT", Transformations.BooleanToInteger(testTakerInf.isStudent));
        sqLiteDatabase.insert("ANONYMOUS_INFORMATION", null, contentValues);
        return id;
    }

    public int addNewAccount(String login, String password, TestTakerInformation testTakerInf) {
        int id = -1;
        cursor = sqLiteDatabase.rawQuery("SELECT ID FROM ACCOUNTS_INFORAMTION ORDER BY ID DESC LIMIT 1", null);
        if (cursor.moveToFirst())
            id = Integer.parseInt(cursor.getString(0)) + 1;
        else
            id = 1;
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("LOGIN", login);
        contentValues.put("PASSWORD", password);
        contentValues.put("AGE", testTakerInf.age);
        contentValues.put("AREA", testTakerInf.area);
        contentValues.put("GENDER", testTakerInf.area);
        contentValues.put("IS_SCHOOLCHILD", Transformations.BooleanToInteger(testTakerInf.isSchoolchild));
        contentValues.put("IS_STUDENT", Transformations.BooleanToInteger(testTakerInf.isStudent));
        sqLiteDatabase.insert("ACCOUNTS_INFORAMTION", null, contentValues);
        return id;
    }
}
