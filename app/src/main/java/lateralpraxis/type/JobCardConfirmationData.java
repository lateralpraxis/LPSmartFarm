package lateralpraxis.type;

public class JobCardConfirmationData {
    private String JobCardDetailId;
    private String VisitDate;
    private String FarmActivity;
    private String FbNurType;
    private String FbNurId;
    private String ActivityValue;
    private String ConfirmedValue;
    private String ActivityType;
    private String PlannedValue;
    private String Uom;
    private String Remarks;

    public JobCardConfirmationData(String jobCardDetailId, String visitDate, String farmActivity, String fbNurType, String fbNurId, String activityValue, String confirmedValue, String activityType, String plannedValue, String uom, String remarks) {
        JobCardDetailId = jobCardDetailId;
        VisitDate = visitDate;
        FarmActivity = farmActivity;
        FbNurType = fbNurType;
        FbNurId = fbNurId;
        ActivityValue = activityValue;
        ConfirmedValue = confirmedValue;
        ActivityType = activityType;
        PlannedValue = plannedValue;
        Uom = uom;
        Remarks = remarks;

    }

    public String getJobCardDetailId() {
        return JobCardDetailId;
    }

    public void setJobCardDetailId(String jobCardDetailId) {
        JobCardDetailId = jobCardDetailId;
    }

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

    public String getFarmActivity() {
        return FarmActivity;
    }

    public void setFarmActivity(String farmActivity) {
        FarmActivity = farmActivity;
    }

    public String getFbNurType() {
        return FbNurType;
    }

    public void setFbNurType(String fbNurType) {
        FbNurType = fbNurType;
    }

    public String getFbNurId() {
        return FbNurId;
    }

    public void setFbNurId(String fbNurId) {
        FbNurId = fbNurId;
    }

    public String getActivityValue() {
        return ActivityValue;
    }

    public void setActivityValue(String actValue) {
        ActivityValue = actValue;
    }


    public String getConfirmedValue() {
        return ConfirmedValue;
    }

    public void setConfirmedValue(String cfvalue) {
        ConfirmedValue = cfvalue;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getPlannedValue() {
        return PlannedValue;
    }

    public void setPlannedValue(String plannedValue) {
        PlannedValue = plannedValue;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
