package lateralpraxis.type;

public class VisitNurseryData {
    private String VisitUniqueId;
    private String NurseryType;
    private String Nursery;
    private String Zone;
    private String Plantation;
    private String VisitDate;

    public VisitNurseryData(String visitUniqueId, String nurseryType, String nursery, String zone, String plantation, String visitDate) {
        VisitUniqueId = visitUniqueId;
        NurseryType = nurseryType;
        Nursery = nursery;
        Zone = zone;
        Plantation = plantation;
        VisitDate = visitDate;
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

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }
}
