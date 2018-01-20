package lateralpraxis.type;

import java.lang.reflect.Member;

/**
 * Created by LPNOIDA01 on 10/16/2017.
 */

public class FamilyMember {
    private String  Id;
    private String MemberName;
    private String Gender;
    private String BirthDate;
    private String Relationship;
    private String IsNominee;
    private String NomineePercentage;

    public FamilyMember (String Id, String MemberName, String Gender, String BirthDate, String Relationship, String IsNominee, String NomineePercentage)
    {
        this.Id = Id;
        this.MemberName = MemberName;
        this.Gender = Gender;
        this.BirthDate = BirthDate;
        this.Relationship = Relationship;
        this.IsNominee = IsNominee;
        this.NomineePercentage = NomineePercentage;
    }

    public String getId() {
        return Id;
    }

    public void setId(String FarmerUniqueId){
        this.Id = Id;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String MemberName){
        this.MemberName = MemberName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender){
        this.Gender = Gender;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate){
        this.BirthDate = BirthDate;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String Relationship){
        this.Relationship = Relationship;
    }

    public String getIsNominee() {
        return IsNominee;
    }

    public void setIsNominee(String IsNominee){
        this.IsNominee = IsNominee;
    }

    public String getNomineePercentage() {
        return NomineePercentage;
    }

    public void setNomineePercentage(String NomineePercentage){
        this.NomineePercentage = NomineePercentage;
    }
}
