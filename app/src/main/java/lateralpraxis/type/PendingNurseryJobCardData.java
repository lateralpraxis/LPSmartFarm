package lateralpraxis.type;

/**
 * Created by Alok on 29-Nov-2017.
 */

public class PendingNurseryJobCardData {
    private String NurseryType;
    private String NurseryName;
    private String UniqueId;
    private String NurseryZone;
    private String ContactPerson;
    private String Mobile;
    private String Address;
    private String Plantation;
    private String PlUniqueId;

    public PendingNurseryJobCardData(String nurType, String nurName, String uniqueId, String nurZone, String contactPerson, String mobile, String address, String plantation, String plUniqueId)
    {
        this.NurseryType = nurType;
        this.NurseryName = nurName;
        this.UniqueId = uniqueId;
        this.NurseryZone = nurZone;
        this.ContactPerson = contactPerson;
        this.Mobile = mobile;
        this.Address = address;
        this.Plantation = plantation;
        this.PlUniqueId = plUniqueId;
    }


    //Contact Person
    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson){
        this.ContactPerson = contactPerson;
    }

    //Nursery Zone
    public String getNurseryZone() {
        return NurseryZone;
    }

    public void setNurseryZone(String nurZone){
        this.NurseryZone = nurZone;
    }

    //Nursery Name
    public String getNurseryName() {
        return NurseryName;
    }

    public void setNurseryName(String nurName){
        this.NurseryName = nurName;
    }

    //Nursery Type
    public String getNurseryType() {
        return NurseryType;
    }

    public void setNurseryType(String nurType){
        this.NurseryType = nurType;
    }

    //UniqueId
    public String getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(String uniqueId){
        this.UniqueId = uniqueId;
    }

    //Mobile
    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile){
        this.Mobile = mobile;
    }

    //Address
    public String getAddress() {
        return Address;
    }

    public void setAddress(String address){
        this.Address = address;
    }

    //Plantation
    public String getPlantation() {
        return Plantation;
    }

    public void setPlantation(String plantation){
        this.Plantation = plantation;
    }

    //PlUniqueId
    public String getPlUniqueId() {
        return PlUniqueId;
    }

    public void setPlUniqueId(String plUniqueId){
        this.PlUniqueId = plUniqueId;
    }

}
