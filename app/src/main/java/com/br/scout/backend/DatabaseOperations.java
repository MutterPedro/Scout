package com.br.scout.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;
import android.util.Log;

import com.br.scout.beans.Obstacle;
import com.br.scout.beans.User;


/**
 * Created by Pedro Mutter on 09/12/15.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 1;
    private static final String DATABASE_NAME = "scout.db";
    private static final String TABLE_USERS = "users";

    public String CREATE_USER_TABLE = "CREATE TABLE "+ TABLE_USERS+"(id INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, photoLink TEXT, points DOUBLE, special BOOLEAN)";
    public String CREATE_OBSTACLE_TABLE = "CREATE TABLE obstacle (id INTEGER PRIMARY KEY, name TEXT, latitude DOUBLE, longitude DOUBLE)";
    public String CREATE_ESTABLISHMENT_TABLE = "CREATE TABLE establishment (id INTEGER PRIMARY KEY, name TEXT, description TEXT, address TEXT, latitude DOUBLE, longitude DOUBLE)";



    public DatabaseOperations(Context context) {
        super(context, DATABASE_NAME, null, database_version);
        SQLiteDatabase db = getWritableDatabase();

    }

    public DatabaseOperations(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("---------------------->"+db.getPath());
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_ESTABLISHMENT_TABLE);
        db.execSQL(CREATE_OBSTACLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS obstacle");
        db.execSQL("DROP TABLE IF EXISTS establishment");
        onCreate(db);
    }

    public void addUser(User user) {

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("photoLink",user.getPhotoLink());
        values.put("points", user.getPoints());
        values.put("special", user.isSpecial());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("users", null, values);
        db.close();
    }

    public void addObstacle(Obstacle obstacle){
        ContentValues values = new ContentValues();
        values.put("name",obstacle.getName());
        values.put("latitude",obstacle.getLatitude());
        values.put("longitude",obstacle.getLongitude());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("obstacle",null,values);
        db.close();
    }
}
