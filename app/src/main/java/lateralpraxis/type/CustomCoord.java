package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/26/2017.
 */

public class CustomCoord {
    private String Id;
    private String Latitude;
    private String Longitude;
    private String Accuracy;

    public CustomCoord(String Id, String Latitude, String Longitude, String Accuracy) {
        this.Id = Id;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Accuracy = Accuracy;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latt) {
        this.Latitude = latt;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String lon) {
        this.Longitude = lon;
    }

    public String getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.Accuracy = accuracy;
    }

}
