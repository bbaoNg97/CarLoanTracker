package my.edu.tarc.carloantracker.CarLoan;

/**
 * Created by Alex on 10/1/2018.
 */

public class CarLoanFile {
    private String id,name,ownerId,carPlate,date;
    private double monthlyPay,payable;
    private String nextDate,insDate,taxDate,cc,type;

    public CarLoanFile() {
    }

    public CarLoanFile(String id, String name, String ownerId,String carPlate, String date, double monthlyPay,double payable,String nextDate, String insDate, String taxDate, String cc, String type) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.carPlate=carPlate;
        this.date = date;
        this.monthlyPay = monthlyPay;
        this.payable=payable;
        this.nextDate = nextDate;
        this.insDate = insDate;
        this.taxDate = taxDate;
        this.cc = cc;
        this.type = type;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMonthlyPay() {
        return monthlyPay;
    }

    public void setMonthlyPay(double monthlyPay) {
        this.monthlyPay = monthlyPay;
    }

    public String getInsDate() {
        return insDate;
    }

    public void setInsDate(String insDate) {
        this.insDate = insDate;
    }

    public String getTaxDate() {
        return taxDate;
    }

    public void setTaxDate(String taxDate) {
        this.taxDate = taxDate;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
