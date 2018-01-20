package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/6/2017.
 */

public class AttachmentDetails {
    private String Id;
    private String DocumentType;
    private String DocumentName;
    private String DocumentNumber;
    private String ImagePath;
    private String ImageName;
    private String UniqueId;
    private String IsSync;

    public AttachmentDetails (String Id , String DocumentType,String DocumentName, String DocumentNumber, String ImagePath, String ImageName,String UniqueId,String IsSync) {
        this.Id = Id;
        this.DocumentType = DocumentType;
        this.DocumentName = DocumentName;
        this.DocumentNumber = DocumentNumber;
        this.ImagePath = ImagePath;
        this.ImageName = ImageName;
        this.UniqueId =UniqueId;
        this.IsSync = IsSync;
    }

    public void setId(String Id){
        this.Id = Id;
    }

    public void setDocumentType(String DocumentType){
        this.DocumentType = DocumentType;
    }

    public void setDocumentName(String DocumentName){
        this.DocumentName = DocumentName;
    }

    public void setDocumentNumber(String DocumentNumber){
        this.DocumentNumber = DocumentNumber;
    }

    public void setImagePath(String ImagePath){
        this.ImagePath = ImagePath;
    }

    public void setImageName(String ImageName){
        this.ImageName = ImageName;
    }

    public void setUniqueId(String UniqueId){
        this.UniqueId = UniqueId;
    }

    public void setIsSync(String IsSync){
        this.IsSync = IsSync;
    }


    public String getId() {
        return Id;
    }

    public String getDocumentType() {
        return DocumentType;
    }

    public String getDocumentName() {
        return DocumentName;
    }

    public String getDocumentNumber() {
        return DocumentNumber;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public String getImageName() {
        return ImageName;
    }

    public String getUniqueId() {
        return UniqueId;
    }

    public String getIsSync() {
        return IsSync;
    }

}
