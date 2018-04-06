package com.sandfoil.virtualclass.virtual_class_model;

public class FACULTY {
    private String Name;
    private String Department;
    private String Password;

    public FACULTY(){

    }

    public FACULTY(String Name,String Department, String Password){
        this.Name = Name;
        this.Department = Department;
        this.Password = Password;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getDepartment() {
        return Department;
    }
    public void setDepartment(String department) {
        Department = department;
    }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }
}
