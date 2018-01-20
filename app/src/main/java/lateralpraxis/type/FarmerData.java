package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/10/2017.
 */

public class FarmerData {
    private String FarmerUniqueId;
    private String FarmerName;
    private String FatherName;
    private String FarmerCode;
    private String Mobile;
    private String Address;
    private String IsDuplicate;


    public FarmerData (String FarmerUniqueId, String FarmerName, String FatherName, String FarmerCode, String Mobile, String Address,String IsDuplicate)
    {
        this.FarmerUniqueId = FarmerUniqueId;
        this.FarmerName = FarmerName;
        this.FatherName = FatherName;
        this.FarmerCode = FarmerCode;
        this.Mobile = Mobile;
        this.Address = Address;
        this.IsDuplicate = IsDuplicate;
    }

    public String getFarmerUniqueId() {
        return FarmerUniqueId;
    }

    public void setFarmerUniqueId(String FarmerUniqueId){
        this.FarmerUniqueId = FarmerUniqueId;
    }

    public String getFarmerName() {
        return FarmerName;
    }

    public void setFarmerName(String FarmerName){
        this.FarmerName = FarmerName;
    }

    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String FatherName){
        this.FatherName = FatherName;
    }

    public String getFarmerCode() {
        return FarmerCode;
    }

    public void setFarmerCode(String FarmerCode){
        this.FarmerCode = FarmerCode;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile){
        this.Mobile = Mobile;
    }

    public String getAddress() {
        return Address;
    }

    public String getIsDuplicate() {
        return IsDuplicate;
    }

    public void setAddress(String Address){
        this.Address = Address;
    }

    public void setIsDuplicate(String IsDuplicate){
        this.IsDuplicate = IsDuplicate;
    }
}
