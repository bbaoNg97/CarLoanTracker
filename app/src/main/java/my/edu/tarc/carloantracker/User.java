package my.edu.tarc.carloantracker;

import java.util.Date;

/**
 * Created by Bbao on 9/1/2018.
 */

public class User {
    private String userID,firstName,lastName;
    private String nric;
    private String dob;
    private String email;
    private String phoneNo;
    private double salary;
    private String username,password;


    public User() {
    }

    public User(String userID, String firstName, String lastName, String nric, String dob, String email, String phoneNo, double salary, String username, String password) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nric = nric;
        this.dob = dob;
        this.email = email;
        this.phoneNo = phoneNo;
        this.salary = salary;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nric='" + nric + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", phoneNo=" + phoneNo +
                ", salary=" + salary +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
