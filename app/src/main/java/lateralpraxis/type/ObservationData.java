package lateralpraxis.type;
public class ObservationData {
    private String  Id;
    private String DefectId;
    private String Title;
    private String Remark;
    private String IsAttachment;
    private String IsSync;

    public ObservationData(String id, String defectId, String title, String remark, String isAttachment, String isSync) {
        Id = id;
        DefectId = defectId;
        Title = title;
        Remark = remark;
        IsAttachment = isAttachment;
        IsSync = isSync;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDefectId() {
        return DefectId;
    }

    public void setDefectId(String defectId) {
        DefectId = defectId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getIsAttachment() {
        return IsAttachment;
    }

    public void setIsAttachment(String isAttachment) {
        IsAttachment = isAttachment;
    }

    public String getIsSync() {
        return IsSync;
    }

    public void setIsSync(String isSync) {
        IsSync = isSync;
    }
}
