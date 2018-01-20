package lateralpraxis.type;

/**
 * Created by amol.marathe on 30-10-2017.
 */

public class PlantationData {
    private String PlantationUniqueId;
    private String FarmerUniqueId;
    private String FarmerName;
    private String FarmBlockUniqueId;
    private String ZoneId;
    private String Zone;
    private String CropId;
    private String Crop;
    private String CropVarietyId;
    private String CropVariety;
    private String PlantTypeId;
    private String PlantType;
    private String MonthAgeId;
    private String MonthAge;
    private String Acreage;
    private String PlantationDate;
    private String PlantationSystemId;
    private String PlantationSystem;
    private String PlantRow;
    private String PlantColumn;
    private String Balance;
    private String TotalPlant;

    public PlantationData(String plantationUniqueId, String farmerUniqueId, String farmerName, String farmBlockUniqueId, String zoneId, String zone, String cropId, String crop,
                          String cropVarietyId, String cropVariety, String plantTypeId, String plantType, String monthAgeId, String monthAge, String acreage, String plantationDate,
                          String plantationSystemId, String plantationSystem, String plantRow, String plantColumn, String balance, String totalPlant) {
        this.PlantationUniqueId = plantationUniqueId;
        this.FarmerUniqueId = farmerUniqueId;
        this.FarmerName = farmerName;
        this.FarmBlockUniqueId = farmBlockUniqueId;
        this.ZoneId = zoneId;
        this.Zone = zone;
        this.CropId = cropId;
        this.Crop = crop;
        this.CropVarietyId = cropVarietyId;
        this.CropVariety = cropVariety;
        this.PlantTypeId = plantTypeId;
        this.PlantType = plantType;
        this.MonthAgeId = monthAgeId;
        this.MonthAge = monthAge;
        this.Acreage = acreage;
        this.PlantationDate = plantationDate;
        this.PlantationSystemId = plantationSystemId;
        this.PlantationSystem = plantationSystem;
        this.PlantRow = plantRow;
        this.PlantColumn = plantColumn;
        this.Balance = balance;
        this.TotalPlant = totalPlant;
    }

    //<editor-fold desc="Get and Set PlantationUniqueId">
    public String getPlantationUniqueId() { return PlantationUniqueId; }
    public void setPlantationUniqueId (String plantationUniqueId) { this.PlantationUniqueId = plantationUniqueId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set FarmerUniqueId">
    public String getFarmerUniqueId() { return FarmerUniqueId; }
    public void setFarmerUniqueId (String farmerUniqueId) { this.FarmerUniqueId = farmerUniqueId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set FarmerName">
    public String getFarmerName() { return FarmerName; }
    public void setFarmerName (String farmerName) { this.FarmerName = farmerName; }
    //</editor-fold>

    //<editor-fold desc="Get and Set FarmBlockUniqueId">
    public String getFarmBlockUniqueId() { return FarmBlockUniqueId; }
    public void setFarmBlockUniqueId (String farmBlockUniqueId) { this.FarmBlockUniqueId = farmBlockUniqueId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set ZoneId">
    public String getZoneId() { return ZoneId; }
    public void setZoneId (String zoneId) { this.ZoneId = zoneId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set Zone">
    public String getZone() { return Zone; }
    public void setZone (String zone) { this.Zone = zone; }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropId">
    public String getCropId() { return CropId; }
    public void setCropId (String cropId) { this.CropId = cropId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set Crop">
    public String getCrop() { return Crop; }
    public void setCrop (String crop) { this.Crop = crop; }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropVarietyId">
    public String getCropVarietyId() { return CropVarietyId; }
    public void setCropVarietyId (String cropVarietyId) { this.CropVarietyId = cropVarietyId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropVariety">
    public String getCropVariety() { return CropVariety; }
    public void setCropVariety (String cropVariety) { this.CropVariety = cropVariety; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantTypeId">
    public String getPlantTypeId() { return PlantTypeId; }
    public void setPlantTypeId (String plantTypeId) { this.PlantTypeId = plantTypeId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantType">
    public String getPlantType() { return PlantType; }
    public void setPlantType (String plantType) { this.PlantType = plantType; }
    //</editor-fold>

    //<editor-fold desc="Get and Set MonthAgeId">
    public String getMonthAgeId() { return MonthAgeId; }
    public void setMonthAgeId (String monthAgeId) { this.MonthAgeId = monthAgeId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set MonthAge">
    public String getMonthAge() { return MonthAge; }
    public void setMonthAge (String monthAge) { this.MonthAge = monthAge; }
    //</editor-fold>

    //<editor-fold desc="Get and Set Acreage">
    public String getAcreage() { return Acreage; }
    public void setAcreage (String acreage) { this.Acreage = acreage; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantationDate">
    public String getPlantationDate() { return PlantationDate; }
    public void setPlantationDate (String plantationDate) { this.PlantationDate = plantationDate; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantationSystemId">
    public String getPlantationSystemId() { return PlantationSystemId; }
    public void setPlantationSystemId (String plantationSystemId) { this.PlantationSystemId = plantationSystemId; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantationSystem">
    public String getPlantationSystem() { return PlantationSystem; }
    public void setPlantationSystem (String plantationSystem) { this.PlantationSystem = plantationSystem; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantRow">
    public String getPlantRow() { return PlantRow; }
    public void setPlantRow (String plantRow) { this.PlantRow= plantRow; }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantColumn">
    public String getPlantColumn() { return PlantColumn; }
    public void setPlantColumn (String plantColumn) { this.PlantColumn = plantColumn; }
    //</editor-fold>

    //<editor-fold desc="Get and Set Balance">
    public String getBalance() { return Balance; }
    public void setBalance (String balance) { this.Balance = balance; }
    //</editor-fold>

    //<editor-fold desc="Get and Set TotalPlant">
    public String getTotalPlant() { return TotalPlant; }
    public void setTotalPlant (String totalPlant) { this.TotalPlant = totalPlant; }
    //</editor-fold>
}
