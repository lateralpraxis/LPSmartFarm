package lateralpraxis.type;

public class FarmBlockViewData {
    private String FarmBlockCode;
    private String FarmerId;
    private String FPOId;
    private String FPO;
    private String Street1;
    private String Street2;
    private String StateId;
    private String StateName;
    private String DistrictId;
    private String DistrictName;
    private String BlockId;
    private String BlockName;
    private String PanchayatId;
    private String PanchayatName;
    private String VillageId;
    private String VillageName;
    private String PinCodeId;
    private String PinCode;
    private String AddressType;
    private String KhataNo;
    private String KhasraNo;
    private String ContractDate;
    private String Acerage;
    private String SoilTypeId;
    private String SoilType;
    private String ElevationMSL;
    private String PHChemical;
    private String Nitrogen;
    private String Potash;
    private String Phosphorus;
    private String OrganicCarbonPerc;
    private String Magnesium;
    private String Calcium;
    private String ExistingUseId;
    private String ExistingUse;
    private String CommunityUseId;
    private String CommunityUse;
    private String ExistingHazard;
    private String ExistingHazardId;
    private String RiverId;
    private String River;
    private String DamId;
    private String Dam;
    private String IrrigationId;
    private String Irrigation;
    private String OverheadTransmission;
    private String LegalDisputeId;
    private String LegalDispute;
    private String SourceWaterId;
    private String SourceWater;
    private String ElectricitySourceId;
    private String ElectricitySource;
    private String DripperSpacing;
    private String DischargeRate;
    private String OwnershipTypeId;
    private String OwnershipType;
    private String OwnerName;
    private String OwnerMobile;

    public FarmBlockViewData(String farmBlockCode, String farmerId, String FPOId, String FPO, String street1, String street2, String stateId, String stateName, String districtId, String districtName, String blockId, String blockName, String panchayatId, String panchayatName, String villageId, String villageName, String pinCodeId, String pinCode, String addressType, String khataNo, String khasraNo, String contractDate, String acerage, String soilTypeId, String soilType, String elevationMSL, String PHChemical, String nitrogen, String potash, String phosphorus, String organicCarbonPerc, String magnesium, String calcium, String existingUseId, String existingUse, String communityUseId, String communityUse, String existingHazard, String existingHazardId, String riverId, String river, String damId, String dam, String irrigationId, String irrigation, String overheadTransmission, String legalDisputeId, String legalDispute, String sourceWaterId, String sourceWater, String electricitySourceId, String electricitySource, String dripperSpacing, String dischargeRate, String ownershipTypeId, String ownershipType, String ownerName, String ownerMobile) {
        FarmBlockCode = farmBlockCode;
        FarmerId = farmerId;
        this.FPOId = FPOId;
        this.FPO = FPO;
        Street1 = street1;
        Street2 = street2;
        StateId = stateId;
        StateName = stateName;
        DistrictId = districtId;
        DistrictName = districtName;
        BlockId = blockId;
        BlockName = blockName;
        PanchayatId = panchayatId;
        PanchayatName = panchayatName;
        VillageId = villageId;
        VillageName = villageName;
        PinCodeId = pinCodeId;
        PinCode = pinCode;
        AddressType = addressType;
        KhataNo = khataNo;
        KhasraNo = khasraNo;
        ContractDate = contractDate;
        Acerage = acerage;
        SoilTypeId = soilTypeId;
        SoilType = soilType;
        ElevationMSL = elevationMSL;
        this.PHChemical = PHChemical;
        Nitrogen = nitrogen;
        Potash = potash;
        Phosphorus = phosphorus;
        OrganicCarbonPerc = organicCarbonPerc;
        Magnesium = magnesium;
        Calcium = calcium;
        ExistingUseId = existingUseId;
        ExistingUse = existingUse;
        CommunityUseId = communityUseId;
        CommunityUse = communityUse;
        ExistingHazard = existingHazard;
        ExistingHazardId = existingHazardId;
        RiverId = riverId;
        River = river;
        DamId = damId;
        Dam = dam;
        IrrigationId = irrigationId;
        Irrigation = irrigation;
        OverheadTransmission = overheadTransmission;
        LegalDisputeId = legalDisputeId;
        LegalDispute = legalDispute;
        SourceWaterId = sourceWaterId;
        SourceWater = sourceWater;
        ElectricitySourceId = electricitySourceId;
        ElectricitySource = electricitySource;
        DripperSpacing = dripperSpacing;
        DischargeRate = dischargeRate;
        OwnershipTypeId = ownershipTypeId;
        OwnershipType = ownershipType;
        OwnerName = ownerName;
        OwnerMobile = ownerMobile;
    }

    public String getFarmBlockCode() {
        return FarmBlockCode;
    }

    public void setFarmBlockCode(String farmBlockCode) {
        FarmBlockCode = farmBlockCode;
    }

    public String getFarmerId() {
        return FarmerId;
    }

    public void setFarmerId(String farmerId) {
        FarmerId = farmerId;
    }

    public String getFPOId() {
        return FPOId;
    }

    public void setFPOId(String FPOId) {
        this.FPOId = FPOId;
    }

    public String getFPO() {
        return FPO;
    }

    public void setFPO(String FPO) {
        this.FPO = FPO;
    }

    public String getStreet1() {
        return Street1;
    }

    public void setStreet1(String street1) {
        Street1 = street1;
    }

    public String getStreet2() {
        return Street2;
    }

    public void setStreet2(String street2) {
        Street2 = street2;
    }

    public String getStateId() {
        return StateId;
    }

    public void setStateId(String stateId) {
        StateId = stateId;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getDistrictId() {
        return DistrictId;
    }

    public void setDistrictId(String districtId) {
        DistrictId = districtId;
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }

    public String getBlockId() {
        return BlockId;
    }

    public void setBlockId(String blockId) {
        BlockId = blockId;
    }

    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String blockName) {
        BlockName = blockName;
    }

    public String getPanchayatId() {
        return PanchayatId;
    }

    public void setPanchayatId(String panchayatId) {
        PanchayatId = panchayatId;
    }

    public String getPanchayatName() {
        return PanchayatName;
    }

    public void setPanchayatName(String panchayatName) {
        PanchayatName = panchayatName;
    }

    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(String villageId) {
        VillageId = villageId;
    }

    public String getVillageName() {
        return VillageName;
    }

    public void setVillageName(String villageName) {
        VillageName = villageName;
    }

    public String getPinCodeId() {
        return PinCodeId;
    }

    public void setPinCodeId(String pinCodeId) {
        PinCodeId = pinCodeId;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }

    public String getAddressType() {
        return AddressType;
    }

    public void setAddressType(String addressType) {
        AddressType = addressType;
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

    public String getContractDate() {
        return ContractDate;
    }

    public void setContractDate(String contractDate) {
        ContractDate = contractDate;
    }

    public String getAcerage() {
        return Acerage;
    }

    public void setAcerage(String acerage) {
        Acerage = acerage;
    }

    public String getSoilTypeId() {
        return SoilTypeId;
    }

    public void setSoilTypeId(String soilTypeId) {
        SoilTypeId = soilTypeId;
    }

    public String getSoilType() {
        return SoilType;
    }

    public void setSoilType(String soilType) {
        SoilType = soilType;
    }

    public String getElevationMSL() {
        return ElevationMSL;
    }

    public void setElevationMSL(String elevationMSL) {
        ElevationMSL = elevationMSL;
    }

    public String getPHChemical() {
        return PHChemical;
    }

    public void setPHChemical(String PHChemical) {
        this.PHChemical = PHChemical;
    }

    public String getNitrogen() {
        return Nitrogen;
    }

    public void setNitrogen(String nitrogen) {
        Nitrogen = nitrogen;
    }

    public String getPotash() {
        return Potash;
    }

    public void setPotash(String potash) {
        Potash = potash;
    }

    public String getPhosphorus() {
        return Phosphorus;
    }

    public void setPhosphorus(String phosphorus) {
        Phosphorus = phosphorus;
    }

    public String getOrganicCarbonPerc() {
        return OrganicCarbonPerc;
    }

    public void setOrganicCarbonPerc(String organicCarbonPerc) {
        OrganicCarbonPerc = organicCarbonPerc;
    }

    public String getMagnesium() {
        return Magnesium;
    }

    public void setMagnesium(String magnesium) {
        Magnesium = magnesium;
    }

    public String getCalcium() {
        return Calcium;
    }

    public void setCalcium(String calcium) {
        Calcium = calcium;
    }

    public String getExistingUseId() {
        return ExistingUseId;
    }

    public void setExistingUseId(String existingUseId) {
        ExistingUseId = existingUseId;
    }

    public String getExistingUse() {
        return ExistingUse;
    }

    public void setExistingUse(String existingUse) {
        ExistingUse = existingUse;
    }

    public String getCommunityUseId() {
        return CommunityUseId;
    }

    public void setCommunityUseId(String communityUseId) {
        CommunityUseId = communityUseId;
    }

    public String getCommunityUse() {
        return CommunityUse;
    }

    public void setCommunityUse(String communityUse) {
        CommunityUse = communityUse;
    }

    public String getExistingHazard() {
        return ExistingHazard;
    }

    public void setExistingHazard(String existingHazard) {
        ExistingHazard = existingHazard;
    }

    public String getExistingHazardId() {
        return ExistingHazardId;
    }

    public void setExistingHazardId(String existingHazardId) {
        ExistingHazardId = existingHazardId;
    }

    public String getRiverId() {
        return RiverId;
    }

    public void setRiverId(String riverId) {
        RiverId = riverId;
    }

    public String getRiver() {
        return River;
    }

    public void setRiver(String river) {
        River = river;
    }

    public String getDamId() {
        return DamId;
    }

    public void setDamId(String damId) {
        DamId = damId;
    }

    public String getDam() {
        return Dam;
    }

    public void setDam(String dam) {
        Dam = dam;
    }

    public String getIrrigationId() {
        return IrrigationId;
    }

    public void setIrrigationId(String irrigationId) {
        IrrigationId = irrigationId;
    }

    public String getIrrigation() {
        return Irrigation;
    }

    public void setIrrigation(String irrigation) {
        Irrigation = irrigation;
    }

    public String getOverheadTransmission() {
        return OverheadTransmission;
    }

    public void setOverheadTransmission(String overheadTransmission) {
        OverheadTransmission = overheadTransmission;
    }

    public String getLegalDisputeId() {
        return LegalDisputeId;
    }

    public void setLegalDisputeId(String legalDisputeId) {
        LegalDisputeId = legalDisputeId;
    }

    public String getLegalDispute() {
        return LegalDispute;
    }

    public void setLegalDispute(String legalDispute) {
        LegalDispute = legalDispute;
    }

    public String getSourceWaterId() {
        return SourceWaterId;
    }

    public void setSourceWaterId(String sourceWaterId) {
        SourceWaterId = sourceWaterId;
    }

    public String getSourceWater() {
        return SourceWater;
    }

    public void setSourceWater(String sourceWater) {
        SourceWater = sourceWater;
    }

    public String getElectricitySourceId() {
        return ElectricitySourceId;
    }

    public void setElectricitySourceId(String electricitySourceId) {
        ElectricitySourceId = electricitySourceId;
    }

    public String getElectricitySource() {
        return ElectricitySource;
    }

    public void setElectricitySource(String electricitySource) {
        ElectricitySource = electricitySource;
    }

    public String getDripperSpacing() {
        return DripperSpacing;
    }

    public void setDripperSpacing(String dripperSpacing) {
        DripperSpacing = dripperSpacing;
    }

    public String getDischargeRate() {
        return DischargeRate;
    }

    public void setDischargeRate(String dischargeRate) {
        DischargeRate = dischargeRate;
    }

    public String getOwnershipTypeId() {
        return OwnershipTypeId;
    }

    public void setOwnershipTypeId(String ownershipTypeId) {
        OwnershipTypeId = ownershipTypeId;
    }

    public String getOwnershipType() {
        return OwnershipType;
    }

    public void setOwnershipType(String ownershipType) {
        OwnershipType = ownershipType;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerMobile() {
        return OwnerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        OwnerMobile = ownerMobile;
    }
}