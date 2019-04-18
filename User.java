package com.example.dmberry.HereIAm;

public class User {

    private String lon;
    private String lat;
    //private String id            making the user the id instead
    private String codeName;
    private String username;
    private String timeDate;

    public User(String username,String lon, String lat,String codeName,String timeDate)
    {
        this.lat=lat;
        this.lon=lon;
        this.username=username;
        this.codeName=codeName;
        this.timeDate=timeDate;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
