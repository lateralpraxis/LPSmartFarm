package lateralpraxis.type;

public class VisitViewData {
    private String FarmerUniqueId;
    private String FarmBlockUniqueId;
    private String PlantationId;
    private String FarmBlockCode;
    private String Farmer;
    private String Mobile;
    private String FarmerCode;
    private String Plantation;
    private String PlantHeight;
    private String PlantStatusId;
    private String PlantStatus;
    private String Days;

    public VisitViewData(String farmerUniqueId, String farmBlockUniqueId, String plantationId, String farmBlockCode, String farmer, String mobile, String farmerCode, String plantation, String plantHeight, String plantStatusId, String plantStatus, String days) {
        FarmerUniqueId = farmerUniqueId;
        FarmBlockUniqueId = farmBlockUniqueId;
        PlantationId = plantationId;
        FarmBlockCode = farmBlockCode;
        Farmer = farmer;
        Mobile = mobile;
        FarmerCode = farmerCode;
        Plantation = plantation;
        PlantHeight = plantHeight;
        PlantStatusId = plantStatusId;
        PlantStatus = plantStatus;
        Days = days;
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

    public String getPlantHeight() {
        return PlantHeight;
    }

    public void setPlantHeight(String plantHeight) {
        PlantHeight = plantHeight;
    }

    public String getPlantStatusId() {
        return PlantStatusId;
    }

    public void setPlantStatusId(String plantStatusId) {
        PlantStatusId = plantStatusId;
    }

    public String getPlantStatus() {
        return PlantStatus;
    }

    public void setPlantStatus(String plantStatus) {
        PlantStatus = plantStatus;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }
}
