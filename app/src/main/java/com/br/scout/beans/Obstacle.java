package com.br.scout.beans;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by root on 17/12/15.
 */
public class Obstacle implements Comparable{

    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private User user;
    private double distance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

        @Override
    public int compareTo(Object another) {
        if(this.distance == ((Obstacle)another).getDistance()) {
            return 0;
        }
        return this.distance < ((Obstacle)another).getDistance() ? -1: 1;
    }
}
