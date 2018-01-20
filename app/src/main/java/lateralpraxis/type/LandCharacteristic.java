package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/26/2017.
 */

public class LandCharacteristic {
    private String Id;
    private String Title;
    private String CharacteristicId;

    public LandCharacteristic (String Id, String Title, String CharacteristicId)
    {
        this.Id = Id;
        this.Title = Title;
        this.CharacteristicId = CharacteristicId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id){
        this.Id = Id;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title){
        this.Title = Title;
    }


    public String getCharacteristicId() {
        return CharacteristicId;
    }

    public void setCharacteristicId(String CharacteristicId){
        this.CharacteristicId = CharacteristicId;
    }
}
