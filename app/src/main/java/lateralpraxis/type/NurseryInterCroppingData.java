package lateralpraxis.type;

/**
 * Created by amol.marathe on 07-11-2017.
 */

public class NurseryInterCroppingData {
    private String Id;
    private String InterCroppingUniqueId;
    private String NurseryUniqueId;
    private String NurseryId;
    private String NurseryType;
    private String NurseryName;
    private String PlantationUniqueId;
    private String ZoneId;
    private String Zone;
    private String CropId;
    private String Crop;
    private String CropVarietyId;
    private String CropVariety;
    private String SeasonId;
    private String Season;
    private String Acreage;
    private String IsActive;
    private String IsSync;

    //<editor-fold desc="Class Constructor">
    public NurseryInterCroppingData() {
    }

    public NurseryInterCroppingData(String id, String interCroppingUniqueId, String nurseryUniqueId, String nurseryId, String nurseryType, String nurseryName, String plantationUniqueId,
                                    String zoneId, String zone, String cropId, String crop, String cropVarietyId, String cropVariety,
                                    String seasonId, String season, String acreage, String isActive, String isSync) {
        this.Id = id;
        this.InterCroppingUniqueId = interCroppingUniqueId;
        this.NurseryUniqueId = nurseryUniqueId;
        this.NurseryId = nurseryId;
        this.NurseryType = nurseryType;
        this.NurseryName = nurseryName;
        this.PlantationUniqueId = plantationUniqueId;
        this.ZoneId = zoneId;
        this.Zone = zone;
        this.CropId = cropId;
        this.Crop = crop;
        this.CropVarietyId = cropVarietyId;
        this.CropVariety = cropVariety;
        this.SeasonId = seasonId;
        this.Season = season;
        this.Acreage = acreage;
        this.IsActive = isActive;
        this.IsSync = isSync;
    }

    //</editor-fold>

    //<editor-fold desc="Get and Set Id">
    public String getId() {
        return this.Id;
    }

    public void setId(String id) {
        this.Id = id;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set InterCroppingUniqueId">
    public String getInterCroppingUniqueId() {
        return this.InterCroppingUniqueId;
    }

    public void setInterCroppingUniqueId(String interCroppingUniqueId) {
        this.InterCroppingUniqueId = interCroppingUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set NurseryId">
    public String getNurseryUniqueId() {
        return this.NurseryUniqueId;
    }

    public void setNurseryUniqueId(String nurseryUniqueId) {
        this.NurseryUniqueId = nurseryUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set NurseryId">
    public String getNurseryId() {
        return this.NurseryId;
    }

    public void setNurseryId(String nurseryId) {
        this.NurseryId = nurseryId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set NurseryId">
    public String getNurseryType() {
        return this.NurseryType;
    }

    public void setNurseryType(String nurseryType) {
        this.NurseryType = nurseryType;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set NurseryName">
    public String getNurseryName() {
        return this.NurseryName;
    }

    public void setNurseryName(String nurseryName) {
        this.NurseryName = nurseryName;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set PlantationUniqueId">
    public String getPlantationUniqueId() {
        return this.PlantationUniqueId;
    }

    public void setPlantationUniqueId(String plantationUniqueId) {
        this.PlantationUniqueId = plantationUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set ZoneId">
    public String getZoneId() {
        return this.ZoneId;
    }

    public void setZoneId(String zoneId) {
        this.ZoneId = zoneId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set Zone">
    public String getZone() {
        return this.Zone;
    }

    public void setZone(String zone) {
        this.Zone = zone;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropId">
    public String getCropId() {
        return this.CropId;
    }

    public void setCropId(String cropId) {
        this.CropId = cropId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set Crop">
    public String getCrop() {
        return this.Crop;
    }

    public void setCrop(String crop) {
        this.Crop = crop;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropVarietyId">
    public String getCropVarietyId() {
        return this.CropVarietyId;
    }

    public void setCropVarietyId(String cropVarietyId) {
        this.CropVarietyId = CropVarietyId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set CropVariety">
    public String getCropVariety() {
        return this.CropVariety;
    }

    public void setCropVariety(String cropVariety) {
        this.CropVariety = cropVariety;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set Season">
    public String getSeason() {
        return this.Season;
    }

    public void setSeason(String season) {
        this.Season = season;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set SeasonId">
    public String getSeasonId() {
        return this.SeasonId;
    }

    public void setSeasonId(String seasonId) {
        this.SeasonId = seasonId;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set Acreage">
    public String getAcreage() {
        return this.Acreage;
    }

    public void setAcreage(String acreage) {
        this.Acreage = acreage;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set IsActive">
    public String getIsActive() {
        return this.IsActive;
    }

    public void setIsActive(String isActive) {
        this.IsActive = isActive;
    }
    //</editor-fold>

    //<editor-fold desc="Get and Set IsSync">
    public String getIsSync() {
        return this.IsSync;
    }

    public void setIsSync(String isSync) {
        this.IsSync = isSync;
    }
    //</editor-fold>
}
