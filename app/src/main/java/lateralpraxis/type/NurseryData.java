package lateralpraxis.type;

/**
 * Created by Alok on 17-Nov-2017.
 */

public class NurseryData {
    private String NurseryType;
    private String NurseryId;
    private String NurseryUniqueId;
    private String NurseryName;
    private String KhataNo;
    private String KhasraNo;
    private String Address;
    private String NurseryArea;

    public NurseryData (String NurseryType, String NurseryId, String NurseryUniqueId, String NurseryName,  String KhataNo, String KhasraNo, String Address, String NurseryArea)
    {
        this.NurseryType = NurseryType;
        this.NurseryId = NurseryId;
        this.NurseryUniqueId = NurseryUniqueId;
        this.NurseryName = NurseryName;
        this.KhataNo = KhataNo;
        this.KhasraNo = KhasraNo;
        this.Address = Address;
        this.NurseryArea = NurseryArea;
    }

    //Nursery Type
    public String getNurseryType() {
        return NurseryType;
    }

    public void setNurseryType(String NurseryType){
        this.NurseryType = NurseryType;
    }

    //Nursery Id
    public String getNurseryId() {
        return NurseryId;
    }

    public void setNurseryId(String NurseryId){
        this.NurseryId = NurseryId;
    }

    //Nursery UniqueId
    public String getNurseryUniqueId() {
        return NurseryUniqueId;
    }

    public void setNurseryUniqueId(String NurseryUniqueId){
        this.NurseryUniqueId = NurseryUniqueId;
    }
    //Nursery Name
    public String getNurseryName() {
        return NurseryName;
    }

    public void setNurseryName(String NurseryName){
        this.NurseryName = NurseryName;
    }

    //KhataNo
    public String getKhataNo() {
        return KhataNo;
    }

    public void setKhataNo(String KhataNo){
        this.KhataNo = KhataNo;
    }

    //KhasraNo
    public String getKhasraNo() {
        return KhasraNo;
    }

    public void setKhasraNo(String KhasraNo){
        this.KhasraNo = KhasraNo;
    }

    //Nursery Area
    public String getNurseryArea() {
        return NurseryArea;
    }

    public void setNurseryArea(String NurseryArea){
        this.NurseryArea = NurseryArea;
    }

    //Nursery Address
    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address){
        this.Address = Address;
    }

}
