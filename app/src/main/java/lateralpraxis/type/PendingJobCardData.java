package lateralpraxis.type;

/**
 * Created by Alok on 29-Nov-2017.
 */

public class PendingJobCardData {
    private String FarmBlockCode;
    private String UniqueId;
    private String FarmerCode;
    private String FarmerName;
    private String FatherName;
    private String Mobile;
    private String Address;
    private String Plantation;
    private String PlUniqueId;

    public PendingJobCardData (String fbCode, String uniqueId, String farmerCode, String farmerName, String fatherName, String mobile, String address, String plantation, String plUniqueId)
    {
        this.FarmBlockCode = fbCode;
        this.UniqueId = uniqueId;
        this.FarmerCode = farmerCode;
        this.FarmerName = farmerName;
        this.FatherName = fatherName;
        this.Mobile = mobile;
        this.Address = address;
        this.Plantation = plantation;
        this.PlUniqueId = plUniqueId;
    }

    //FB Code
    public String getFarmBlockCode() {
        return FarmBlockCode;
    }

    public void setFarmBlockCode(String fbCode){
        this.FarmBlockCode = fbCode;
    }

    //UniqueId
    public String getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(String uniqueId){
        this.UniqueId = uniqueId;
    }

    //Faremr Code
    public String getFarmerCode() {
        return FarmerCode;
    }

    public void setFarmerCode(String farmerCode){
        this.FarmerCode = farmerCode;
    }

    //FarmerName
    public String getFarmerName() {
        return FarmerName;
    }

    public void setFarmerName(String farmerName){
        this.FarmerName = farmerName;
    }

    //FatherName
    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String fatherName){
        this.FatherName = fatherName;
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
