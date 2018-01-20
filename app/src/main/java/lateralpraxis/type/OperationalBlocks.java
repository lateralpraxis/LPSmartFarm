package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/10/2017.
 */

public class OperationalBlocks {

    private String Id;
    private String DistrictName;
    private String BlockName;

    public OperationalBlocks (String Id, String DistrictName, String BlockName)
    {
        this.Id = Id;
        this.DistrictName = DistrictName;
        this.BlockName = BlockName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id){
        this.Id = Id;
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String DistrictName){
        this.DistrictName = DistrictName;
    }

    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String BlockName){
        this.BlockName = BlockName;
    }
}
