package lateralpraxis.type;

public class VisitData {

    private String VisitUniqueId;
    private String FarmerUniqueId;
    private String FarmBlockUniqueId;
    private String PlantationId;
    private String FarmBlockCode;
    private String Farmer;
    private String Mobile;
    private String FarmerCode;
    private String Plantation;
    private String VisitDate;

    public VisitData(String visitUniqueId, String farmerUniqueId, String farmBlockUniqueId, String plantationId, String farmBlockCode, String farmer, String mobile, String farmerCode, String plantation, String visitDate) {
        VisitUniqueId = visitUniqueId;
        FarmerUniqueId = farmerUniqueId;
        FarmBlockUniqueId = farmBlockUniqueId;
        PlantationId = plantationId;
        FarmBlockCode = farmBlockCode;
        Farmer = farmer;
        Mobile = mobile;
        FarmerCode = farmerCode;
        Plantation = plantation;
        VisitDate = visitDate;
    }

    public String getVisitUniqueId() {
        return VisitUniqueId;
    }

    public void setVisitUniqueId(String visitUniqueId) {
        VisitUniqueId = visitUniqueId;
    }

    public String getFarmerUniqueId() {
        return FarmerUniqueId;
    }

    public void setFarmerUniqueId(String farmerUniqueId) {
        FarmerUniqueId = farmerUniqueId;
    }

    public String getFarmBlockUniqueId() {
        return FarmBlockUniqueId;
    }

    public void setFarmBlockUniqueId(String farmBlockUniqueId) {
        FarmBlockUniqueId = farmBlockUniqueId;
    }

    public String getPlantationId() {
        return PlantationId;
    }

    public void setPlantationId(String plantationId) {
        PlantationId = plantationId;
    }

    public String getFarmBlockCode() {
        return FarmBlockCode;
    }

    public void setFarmBlockCode(String farmBlockCode) {
        FarmBlockCode = farmBlockCode;
    }

    public String getFarmer() {
        return Farmer;
    }

    public void setFarmer(String farmer) {
        Farmer = farmer;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getFarmerCode() {
        return FarmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        FarmerCode = farmerCode;
    }

    public String getPlantation() {
        return Plantation;
    }

    public void setPlantation(String plantation) {
        Plantation = plantation;
    }

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

}
