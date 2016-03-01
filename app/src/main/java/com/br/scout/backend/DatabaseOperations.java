package com.br.scout.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;
import android.util.Log;

import com.br.scout.MapsActivity;
import com.br.scout.beans.Establishment;
import com.br.scout.beans.Obstacle;
import com.br.scout.beans.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Pedro Mutter on 09/12/15.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 1;
    private static final String DATABASE_NAME = "scout.db";
    private static final String TABLE_USERS = "users";

    public String CREATE_USER_TABLE = "CREATE TABLE "+ TABLE_USERS+"(id INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, photoLink TEXT, points DOUBLE, special BOOLEAN)";
    public String CREATE_OBSTACLE_TABLE = "CREATE TABLE obstacle (id INTEGER PRIMARY KEY, name TEXT, latitude DOUBLE, longitude DOUBLE, user_id INTEGER)";
    public String CREATE_ESTABLISHMENT_TABLE = "CREATE TABLE establishment (id INTEGER PRIMARY KEY, name TEXT, description TEXT, address TEXT, latitude DOUBLE, longitude DOUBLE, user_id INTEGER)";



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
        values.put("user_id",obstacle.getUser().getId());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("obstacle",null,values);
        db.close();
    }

    public void addEstablishment(Establishment establishment){
        ContentValues values = new ContentValues();
        values.put("name",establishment.getName());
        values.put("description",establishment.getDescription());
        values.put("address",establishment.getAddress());
        values.put("latitude", establishment.getLatitude());
        values.put("longitude",establishment.getLongitude());
        values.put("user_id",establishment.getUser().getId());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("obstacle",null,values);
        db.close();
    }

    public Obstacle findObstacle(String name) {
        String query = "Select * FROM obstacle WHERE name =  \"" + name + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Obstacle obstacle = new Obstacle();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obstacle.setId(Long.parseLong(cursor.getString(0)));
            obstacle.setName(cursor.getString(1));
            obstacle.setLatitude(Double.parseDouble(cursor.getString(2)));
            obstacle.setLongitude(Double.parseDouble(cursor.getString(3)));
            obstacle.setUser(retrieveUserById(Long.parseLong(cursor.getString(4))));
            cursor.close();
        } else {
            obstacle = null;
        }
        db.close();
        return obstacle;
    }

    public boolean deleteObstacle(String name) {

        boolean result = false;

        String query = "Select * FROM obstacle WHERE name =  \"" + name + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Obstacle obstacle = new Obstacle();

        if (cursor.moveToFirst()) {
            obstacle.setId(Long.parseLong(cursor.getString(0)));
            db.delete("obstacle", "id = ?",
                    new String[] { String.valueOf(obstacle.getId()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public List<Obstacle> listAllObstacles(){
        String query = "Select * FROM obstacle";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        List<Obstacle> list = new ArrayList<Obstacle>();

        while(cursor.moveToNext()) {
            Obstacle obstacle = new Obstacle();
            obstacle.setId(Long.parseLong(cursor.getString(0)));
            obstacle.setName(cursor.getString(1));
            obstacle.setLatitude(Double.parseDouble(cursor.getString(2)));
            obstacle.setLongitude(Double.parseDouble(cursor.getString(3)));

            int distance = calculationByDistance(MapsActivity.MY_LOC, new LatLng(obstacle.getLatitude(), obstacle.getLongitude()));
            obstacle.setDistance(distance);
            if(distance <= 10)
                list.add(obstacle);
        }

        cursor.close();

        Collections.sort(list);
        return list;
    }

    public User retrieveUserById(long id){
        String query = "Select * FROM users WHERE id =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user.setId(Long.parseLong(cursor.getString(0)));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setPhotoLink(cursor.getString(4));
            user.setPoints(Double.parseDouble(cursor.getString(5)));
            user.setSpecial(Boolean.parseBoolean(cursor.getString(6)));
            cursor.close();
        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public User loginUser(String email,String password) {
        String query = "Select * FROM users WHERE email =  \"" + email + "\" AND password = \""+password+"\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user.setId(Long.parseLong(cursor.getString(0)));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setPhotoLink(cursor.getString(4));
            user.setPoints(Double.parseDouble(cursor.getString(5)));
            user.setSpecial(Boolean.parseBoolean(cursor.getString(6)));
            cursor.close();
        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public int calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        //double km = valueResult / 1;
        //DecimalFormat newFormat = new DecimalFormat("####");
        //int kmInDec = Integer.valueOf(newFormat.format(km));
        //double meter = valueResult % 1000;
        //int meterInDec = Integer.valueOf(newFormat.format(meter));
        //Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
        //       + " Meter   " + meterInDec);

        return (int)(valueResult*1000);
    }
}
