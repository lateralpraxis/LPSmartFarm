package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/18/2017.
 */

public class FarmerAssetData {
    private String Id;
    private String Name;
    private String Quantity;

    public FarmerAssetData (String Id, String Name, String Quantity)
    {
        this.Id = Id;
        this.Name = Name;
        this.Quantity = Quantity;
    }

    public String getAssetId() {
        return Id;
    }

    public void setAssetId(String Id){
        this.Id = Id;
    }

    public String getAssetName() {
        return Name;
    }

    public void setAssetName(String Name){
        this.Name = Name;
    }

    public String getAssetQty() {
        return Quantity;
    }

    public void setAssetQty(String Quantity){
        this.Quantity = Quantity;
    }
}
