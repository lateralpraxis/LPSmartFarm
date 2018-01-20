package lateralpraxis.type;

/**
 * Created by Alok on 17-Nov-2017.
 */

public class NurseryZoneData {
    private String NurseryId;
    private String NurseryZoneId;
    private String UniqueId;
    private String NurseryZoneName;
    private String NurseryZoneArea;

    public NurseryZoneData(String NurseryId, String NurseryZoneId, String UniqueId, String
            NurseryZoneName, String NurseryZoneArea) {
        this.NurseryId = NurseryId;
        this.NurseryZoneId = NurseryZoneId;
        this.UniqueId = UniqueId;
        this.NurseryZoneName = NurseryZoneName;
        this.NurseryZoneArea = NurseryZoneArea;
    }

    //Nursery Id
    public String getNurseryId() {
        return NurseryId;
    }

    public void setNurseryId(String NurseryId) {
        this.NurseryId = NurseryId;
    }

    //Nursery Zone Id
    public String getNurseryZoneId() {
        return NurseryZoneId;
    }

    public void setNurseryZoneId(String NurseryZoneId) {
        this.NurseryZoneId = NurseryZoneId;
    }

    //UniqueId
    public String getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(String UniqueId) {
        this.UniqueId = UniqueId;
    }

    //NurseryZoneName
    public String getNurseryZoneName() {
        return NurseryZoneName;
    }

    public void setNurseryZoneName(String NurseryZoneName) {
        this.NurseryZoneName = NurseryZoneName;
    }

    //Nursery Zone Area
    public String getNurseryZoneArea() {
        return NurseryZoneArea;
    }

    public void setNurseryZoneArea(String NurseryZoneArea) {
        this.NurseryZoneArea = NurseryZoneArea;
    }

}
