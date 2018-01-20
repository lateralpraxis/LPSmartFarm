package lateralpraxis.type;

/**
 * Created by LPNOIDA01 on 10/16/2017.
 */

public class LoanDetails {

    private String LoanId;
    private String LoanSource;
    private String LoanType;
    private String ROIPercentage;
    private String LoanAmount;
    private String BalanceAmount;
    private String Tenure;

    public LoanDetails (String LoanId, String LoanSource, String LoanType, String ROIPercentage, String LoanAmount, String BalanceAmount, String Tenure)
    {
        this.LoanId = LoanId;
        this.LoanSource = LoanSource;
        this.LoanType = LoanType;
        this.ROIPercentage = ROIPercentage;
        this.LoanAmount = LoanAmount;
        this.BalanceAmount = BalanceAmount;
        this.Tenure = Tenure;
    }

    public String getLoanId() {
        return LoanId;
    }

    public void setLoanId(String LoanId){
        this.LoanId = LoanId;
    }


    public String getLoanSource() {
        return LoanSource;
    }

    public void setLoanSource(String LoanSource){
        this.LoanSource = LoanSource;
    }

    public String getLoanType() {
        return LoanType;
    }

    public void setLoanType(String LoanType){
        this.LoanType = LoanType;
    }

    public String getROIPercentage() {
        return ROIPercentage;
    }

    public void setROIPercentage(String ROIPercentage){
        this.ROIPercentage = ROIPercentage;
    }

    public String getLoanAmount() {
        return LoanAmount;
    }

    public void setLoanAmount(String LoanAmount){
        this.LoanAmount = LoanAmount;
    }

    public String getBalanceAmount() {
        return BalanceAmount;
    }

    public void setBalanceAmount(String BalanceAmount){
        this.BalanceAmount = BalanceAmount;
    }

    public String getTenure() {
        return Tenure;
    }

    public void setTenure(String Tenure){
        this.Tenure = Tenure;
    }

}
