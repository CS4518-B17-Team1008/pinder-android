package team1008.b17.cs4518.wpi.pinderapp;

/**
 * Created by Marissa on 12/14/2017.
 */

public class UserInformation {

    private String name;
    private String phone;
    private int search_distance;
    private boolean is_owner;
    private boolean is_seeker;
    private int latitude;
    private int longitude;

    public UserInformation() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSearch_distance() {
        return search_distance;
    }

    public void setSearch_distance(int search_distance) {
        this.search_distance = search_distance;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public boolean isIs_seeker() {
        return is_seeker;
    }

    public void setIs_seeker(boolean is_seeker) {
        this.is_seeker = is_seeker;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
