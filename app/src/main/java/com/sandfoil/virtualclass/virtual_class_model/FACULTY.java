package com.sandfoil.virtualclass.virtual_class_model;

public class FACULTY {
    private String Name;
    private String Department;
    private String Password;
    private String facultyId;

    public FACULTY(){

    }

    public FACULTY(String Name,String Department, String Password, String facultyId){
        this.Name = Name;
        this.Department = Department;
        this.Password = Password;
        this.facultyId = facultyId;
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

    public String getFacultyId() {
        return facultyId;
    }
    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }
}
