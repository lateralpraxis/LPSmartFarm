package lateralpraxis.type;

public class VisitNurseryViewData {
    private String VisitUniqueId;
    private String NurseryType;
    private String Nursery;
    private String Zone;
    private String Plantation;
    private String PlantHeight;
    private String PlantStatus;
    private String PlantStatusId;

    public VisitNurseryViewData(String visitUniqueId, String nurseryType, String nursery, String zone, String plantation, String plantHeight, String plantStatus, String plantStatusId) {
        VisitUniqueId = visitUniqueId;
        NurseryType = nurseryType;
        Nursery = nursery;
        Zone = zone;
        Plantation = plantation;
        PlantHeight = plantHeight;
        PlantStatus = plantStatus;
        PlantStatusId = plantStatusId;
    }

    public String getVisitUniqueId() {
        return VisitUniqueId;
    }

    public void setVisitUniqueId(String visitUniqueId) {
        VisitUniqueId = visitUniqueId;
    }

    public String getNurseryType() {
        return NurseryType;
    }

    public void setNurseryType(String nurseryType) {
        NurseryType = nurseryType;
    }

    public String getNursery() {
        return Nursery;
    }

    public void setNursery(String nursery) {
        Nursery = nursery;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
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

    public String getPlantStatus() {
        return PlantStatus;
    }

    public void setPlantStatus(String plantStatus) {
        PlantStatus = plantStatus;
    }

    public String getPlantStatusId() {
        return PlantStatusId;
    }

    public void setPlantStatusId(String plantStatusId) {
        PlantStatusId = plantStatusId;
    }
}
