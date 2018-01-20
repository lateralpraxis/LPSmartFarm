package lateralpraxis.type;

public class FarmBlockData {
    private String FarmBlockUniqueId;
    private String FarmBlockCode;
    private String KhataNo;
    private String KhasraNo;
    private String Acreage;
    private String ContractDate;
    private String Address;

    public FarmBlockData(String farmBlockUniqueId, String farmBlockCode, String acreage, String contractDate, String khataNo, String khasraNo, String address) {
        FarmBlockUniqueId = farmBlockUniqueId;
        FarmBlockCode = farmBlockCode;
        Acreage = acreage;
        ContractDate = contractDate;
        KhataNo = khataNo;
        KhasraNo = khasraNo;
        Address = address;
    }

    public String getFarmBlockUniqueId() {
        return FarmBlockUniqueId;
    }

    public void setFarmBlockUniqueId(String farmBlockUniqueId) {
        FarmBlockUniqueId = farmBlockUniqueId;
    }

    public String getFarmBlockCode() {
        return FarmBlockCode;
    }

    public void setFarmBlockCode(String farmBlockCode) {
        FarmBlockCode = farmBlockCode;
    }

    public String getAcreage() {
        return Acreage;
    }

    public void setAcreage(String acreage) {
        Acreage = acreage;
    }

    public String getContractDate() {
        return ContractDate;
    }

    public void setContractDate(String contractDate) {
        ContractDate = contractDate;
    }

    public String getKhataNo() {
        return KhataNo;
    }

    public void setKhataNo(String khataNo) {
        KhataNo = khataNo;
    }

    public String getKhasraNo() {
        return KhasraNo;
    }

    public void setKhasraNo(String khasraNo) {
        KhasraNo = khasraNo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
