package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 9/27/2017.
 */

public class CustomType {
    private Integer Id;
    private String Name;

    public CustomType(Integer Id, String Name) {
        this.Id = Id;
        this.Name = Name;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(int id) {
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
