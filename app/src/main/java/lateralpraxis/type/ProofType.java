package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/6/2017.
 */

public class ProofType {
    private String Id;
    private String Name;

    public ProofType(String Id, String Name) {
        this.Id = Id;
        this.Name = Name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }


    @Override
    public String toString() {
        return Name;
    }
}