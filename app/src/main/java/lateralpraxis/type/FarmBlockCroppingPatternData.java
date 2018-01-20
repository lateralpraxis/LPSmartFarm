package lateralpraxis.type;

public class FarmBlockCroppingPatternData {
    private String Id;
    private String FarmBlockUniqueId;
    private String CropId;
    private String Crop;
    private String CropVarietyId;
    private String CropVariety;
    private String SeasonId;
    private String Season;
    private String Acreage;

    public FarmBlockCroppingPatternData(String id, String farmBlockUniqueId, String cropId, String crop, String cropVarietyId, String cropVariety, String seasonId, String season, String acreage) {
        Id = id;
        FarmBlockUniqueId = farmBlockUniqueId;
        CropId = cropId;
        Crop = crop;
        CropVarietyId = cropVarietyId;
        CropVariety = cropVariety;
        SeasonId = seasonId;
        Season = season;
        Acreage = acreage;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFarmBlockUniqueId() {
        return FarmBlockUniqueId;
    }

    public void setFarmBlockUniqueId(String farmBlockUniqueId) {
        FarmBlockUniqueId = farmBlockUniqueId;
    }

    public String getCropId() {
        return CropId;
    }

    public void setCropId(String cropId) {
        CropId = cropId;
    }

    public String getCrop() {
        return Crop;
    }

    public void setCrop(String crop) {
        Crop = crop;
    }

    public String getCropVarietyId() {
        return CropVarietyId;
    }

    public void setCropVarietyId(String cropVarietyId) {
        CropVarietyId = cropVarietyId;
    }

    public String getCropVariety() {
        return CropVariety;
    }

    public void setCropVariety(String cropVariety) {
        CropVariety = cropVariety;
    }

    public String getSeasonId() {
        return SeasonId;
    }

    public void setSeasonId(String seasonId) {
        SeasonId = seasonId;
    }

    public String getSeason() {
        return Season;
    }

    public void setSeason(String season) {
        Season = season;
    }

    public String getAcreage() {
        return Acreage;
    }

    public void setAcreage(String acreage) {
        Acreage = acreage;
    }
}
