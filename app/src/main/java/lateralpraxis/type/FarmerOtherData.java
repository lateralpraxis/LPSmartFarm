package lateralpraxis.type;

public class FarmerOtherData {

    private String SoilTypeId;
    private String IrrigationSystemId;
    private String RiverId;
    private String DamId;
    private String WaterSourceId;
    private String ElectricitySourceId;

    public FarmerOtherData(String soilTypeId, String irrigationSystemId, String riverId, String damId, String waterSourceId, String electricitySourceId) {
        SoilTypeId = soilTypeId;
        IrrigationSystemId = irrigationSystemId;
        RiverId = riverId;
        DamId = damId;
        WaterSourceId = waterSourceId;
        ElectricitySourceId = electricitySourceId;
    }

    public String getSoilTypeId() {
        return SoilTypeId;
    }

    public void setSoilTypeId(String soilTypeId) {
        SoilTypeId = soilTypeId;
    }

    public String getIrrigationSystemId() {
        return IrrigationSystemId;
    }

    public void setIrrigationSystemId(String irrigationSystemId) {
        IrrigationSystemId = irrigationSystemId;
    }

    public String getRiverId() {
        return RiverId;
    }

    public void setRiverId(String riverId) {
        RiverId = riverId;
    }

    public String getDamId() {
        return DamId;
    }

    public void setDamId(String damId) {
        DamId = damId;
    }

    public String getWaterSourceId() {
        return WaterSourceId;
    }

    public void setWaterSourceId(String waterSourceId) {
        WaterSourceId = waterSourceId;
    }

    public String getElectricitySourceId() {
        return ElectricitySourceId;
    }

    public void setElectricitySourceId(String electricitySourceId) {
        ElectricitySourceId = electricitySourceId;
    }
}
